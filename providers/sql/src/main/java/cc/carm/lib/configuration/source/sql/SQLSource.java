package cc.carm.lib.configuration.source.sql;

import cc.carm.lib.configuration.commentable.Commentable;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import cc.carm.lib.configuration.source.section.SourcedSection;
import cc.carm.lib.configuration.value.ConfigValue;
import cc.carm.lib.configuration.versioned.VersionedMetaTypes;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLQuery;
import cc.carm.lib.easysql.api.SQLTable;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.*;

public class SQLSource extends ConfigureSource<SourcedSection, Map<String, Object>, SQLSource> {

    protected final @NotNull Gson gson;
    protected final @NotNull SQLManager sqlManager;
    protected final @NotNull String namespace;
    protected final @NotNull SQLTable table;

    protected final @NotNull Map<Integer, SQLValueResolver<?>> resolvers;
    protected SourcedSection rootSection;

    public SQLSource(@NotNull ConfigurationHolder<? extends SQLSource> holder, long lastUpdateMillis,
                     @NotNull Gson gson, @NotNull SQLManager sqlManager,
                     @NotNull Map<Integer, SQLValueResolver<?>> resolvers,
                     @NotNull SQLTable table, @NotNull String namespace) {
        super(holder, lastUpdateMillis);
        this.gson = gson;
        this.sqlManager = sqlManager;
        this.resolvers = resolvers;
        this.namespace = namespace;
        this.table = table;

        try {
            this.table.create(this.sqlManager);
            onReload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected @NotNull SQLSource self() {
        return this;
    }

    public @NotNull Gson gson() {
        return gson;
    }

    @Override
    public @NotNull Map<String, Object> original() {
        return section().data();
    }

    @Override
    public @NotNull SourcedSection section() {
        return Objects.requireNonNull(this.rootSection, "Root section is not initialized.");
    }

    public int purge() throws Exception {
        return this.table.createDelete().addCondition("namespace", namespace).build().execute();
    }

    @Override
    public void save() throws Exception {
        LocalDateTime time = LocalDateTime.now(); // Update time
        List<Object[]> dataValues = new ArrayList<>();

        SourcedSection section = section();
        Map<String, ConfigValue<?, ?>> values = holder().registeredValues();
        for (Map.Entry<String, ConfigValue<?, ?>> entry : values.entrySet()) {
            @NotNull String path = entry.getKey();
            @NotNull ConfigValue<?, ?> config = entry.getValue();
            @Nullable Object value = section.get(path);

            if (value instanceof SourcedSection) {
                value = ((SourcedSection) value).asMap();
            } else if (value instanceof List<?>) {
                List<Object> list = new ArrayList<>();
                for (Object obj : (List<?>) value) {
                    if (obj instanceof SourcedSection) {
                        list.add(((SourcedSection) obj).asMap());
                    } else {
                        list.add(obj);
                    }
                }
                value = list;
            }

            if (value == null) continue;

            try {
                int typeID = typeIdOf(value);
                String data = serialize(typeID, value);
                if (data == null) continue;

                int version = holder().metadata(path).get(VersionedMetaTypes.VERSION, 0);
                dataValues.add(new Object[]{
                        namespace, path, time, version, typeID, data,
                        Commentable.getInlineComment(holder(), path),
                        gson.toJson(Commentable.getHeaderComments(holder(), path)),
                        gson.toJson(Commentable.getFooterComments(holder(), path))
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (holder.option(SQLOptions.PURGE)) {
            purge();
        }
        this.table.createReplaceBatch()
                .setColumnNames(
                        "namespace", "path", "update_time", "version", "type", "value",
                        "inline_comment", "header_comments", "footer_comments"
                ).setAllParams(dataValues).execute();
    }

    @Override
    protected void onReload() throws Exception {
        Map<String, Object> loaded = new LinkedHashMap<>();
        try (SQLQuery query = this.table.createQuery()
                .addCondition("namespace", namespace)
                .build().execute()) {
            ResultSet rs = query.getResultSet();
            while (rs.next()) {
                String path = rs.getString("path");
                if (path == null) continue; // Path should be not null
                int ver = rs.getInt("version");
                try {
                    loaded.put(path, parse(rs.getInt("type"), rs.getString("value")));
                    if (ver != 0) holder().metadata(path).set(VersionedMetaTypes.VERSION, ver);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        this.rootSection = SourcedSection.root(this, loaded);
    }


    protected @Nullable Object parse(int type, String value) throws Exception {
        SQLValueResolver<?> function = this.resolvers.get(type);
        if (function == null) throw new IllegalStateException("No resolvers for type #" + type);
        return function.resolve(this, value);
    }

    protected @Nullable String serialize(int type, @NotNull Object value) throws Exception {
        SQLValueResolver<?> function = this.resolvers.get(type);
        if (function == null) throw new IllegalStateException("No resolvers for type #" + type);
        return function.serialize(this, value);
    }

    protected int typeIdOf(@NotNull Object value) {
        return this.resolvers.entrySet().stream()
                .filter(entry -> entry.getValue().isInstance(value))
                .findFirst().map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("No resolvers for value " + value.getClass().getName()));
    }


}
