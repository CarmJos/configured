package cc.carm.lib.configuration.source.sql;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.commentable.Commentable;
import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.source.ConfigurationFactory;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.versioned.VersionedMetaTypes;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.api.SQLTable;
import cc.carm.lib.easysql.api.builder.TableCreateBuilder;
import cc.carm.lib.easysql.api.enums.IndexType;
import cc.carm.lib.easysql.api.function.SQLHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SQLConfigFactory extends ConfigurationFactory<SQLSource, ConfigurationHolder<SQLSource>, SQLConfigFactory> {

    public static SQLConfigFactory from(@NotNull Supplier<@NotNull SQLManager> managerSupplier) {
        return new SQLConfigFactory(managerSupplier);
    }

    public static SQLConfigFactory from(@NotNull SQLManager manager) {
        return from(() -> manager);
    }

    protected static final @NotNull Gson DEFAULT_GSON = new GsonBuilder()
        .serializeNulls().disableHtmlEscaping().create();

    protected static final @NotNull BiConsumer<String, TableCreateBuilder> DEFAULT_TABLE_SCHEMA = (tableName, builder) -> {
        builder.addColumn("namespace", "VARCHAR(32) NOT NULL");
        builder.addColumn("path", "VARCHAR(96) NOT NULL");

        builder.addColumn("value", "TEXT");

        builder.addColumn("inline_comment", "TEXT");
        builder.addColumn("header_comments", "MEDIUMTEXT");
        builder.addColumn("footer_comments", "MEDIUMTEXT");

        builder.addColumn("type", "TINYINT NOT NULL DEFAULT 0");
        builder.addColumn("version", "MEDIUMINT UNSIGNED NOT NULL DEFAULT 0");

        builder.addColumn(
            "create_time",
            "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP"
        );
        builder.addColumn(
            "update_time",
            "TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
        );

        builder.setIndex(
            IndexType.PRIMARY_KEY, "pk_" + tableName.toLowerCase(),
            "namespace", "path"
        );
        builder.setTableSettings("ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
    };

    protected @NotNull Supplier<SQLManager> managerSupplier;
    protected Supplier<Gson> gsonSupplier = () -> DEFAULT_GSON;

    protected HashMap<Integer, SQLValueResolver<?>> resolvers = new HashMap<>(SQLValueResolver.STANDARD_RESOLVERS);
    protected SQLTable tableName = SQLTable.of("config", (table) -> DEFAULT_TABLE_SCHEMA.accept("config", table));
    protected String namespace = "default";

    public SQLConfigFactory(@NotNull Supplier<SQLManager> managerSupplier) {
        this.managerSupplier = managerSupplier;
    }

    @Override
    protected SQLConfigFactory self() {
        return this;
    }

    public SQLConfigFactory manager(@NotNull Supplier<SQLManager> managerSupplier) {
        this.managerSupplier = managerSupplier;
        return self();
    }

    public SQLConfigFactory manager(@NotNull SQLManager manager) {
        return manager(() -> manager);
    }

    public SQLConfigFactory gson(@NotNull Supplier<Gson> gsonSupplier) {
        this.gsonSupplier = gsonSupplier;
        return self();
    }

    public SQLConfigFactory gson(@NotNull Consumer<GsonBuilder> builder) {
        return gson(() -> {
            GsonBuilder gsonBuilder = new GsonBuilder();
            builder.accept(gsonBuilder);
            return gsonBuilder.create();
        });
    }

    public SQLConfigFactory gson(@NotNull Gson gson) {
        return gson(() -> gson);
    }

    public SQLConfigFactory resolver(@Range(from = 0, to = 255) int type, @NotNull SQLValueResolver<?> resolver) {
        this.resolvers.put(type, resolver);
        return self();
    }

    public <T> SQLConfigFactory resolver(@Range(from = 0, to = 255) int id, @NotNull Class<T> clazz,
                                         @NotNull DataFunction<String, T> parser) {
        return resolver(id, ValueType.of(clazz), parser);
    }

    public <T> SQLConfigFactory resolver(@Range(from = 0, to = 255) int id, @NotNull ValueType<T> type,
                                         @NotNull DataFunction<String, T> parser) {
        return resolver(id, SQLValueResolver.of(type, parser));
    }

    public <T> SQLConfigFactory resolver(@Range(from = 0, to = 255) int id, @NotNull Class<T> clazz,
                                         @NotNull DataFunction<String, T> parser,
                                         @NotNull DataFunction<T, String> serializer) {
        return resolver(id, ValueType.of(clazz), parser, serializer);
    }

    public <T> SQLConfigFactory resolver(@Range(from = 0, to = 255) int id, @NotNull ValueType<T> type,
                                         @NotNull DataFunction<String, T> parser,
                                         @NotNull DataFunction<T, String> serializer) {
        return resolver(id, SQLValueResolver.of(type, parser, serializer));
    }

    public SQLConfigFactory table(@NotNull SQLTable table) {
        this.tableName = table;
        return self();
    }

    public SQLConfigFactory table(@NotNull String tableName, @NotNull SQLHandler<TableCreateBuilder> builder) {
        return table(SQLTable.of(tableName, builder));
    }

    public SQLConfigFactory tableName(@NotNull String tableName) {
        return table(tableName, table -> DEFAULT_TABLE_SCHEMA.accept(tableName, table));
    }

    public SQLConfigFactory namespace(@NotNull String namespace) {
        this.namespace = namespace;
        return self();
    }

    @Override
    public @NotNull ConfigurationHolder<SQLSource> build() {
        Gson gson = gsonSupplier.get();
        if (gson == null) throw new NullPointerException("No Gson instance provided.");

        SQLManager manager = this.managerSupplier.get();
        if (manager == null) throw new NullPointerException("No SQLManager instance provided.");

        Commentable.registerMeta(this.initializer);
        VersionedMetaTypes.register(this.initializer);

        return new ConfigurationHolder<SQLSource>(this.adapters, this.options, this.metadata, this.initializer) {
            final SQLSource source = new SQLSource(
                this, System.currentTimeMillis(),
                gson, manager, resolvers, tableName, namespace
            );

            @Override
            public @NotNull SQLSource config() {
                return source;
            }
        };
    }


}
