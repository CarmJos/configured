package cc.carm.lib.configuration.source.json;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.file.FileConfigSource;
import cc.carm.lib.configuration.source.section.SourcedSection;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class JSONSource extends FileConfigSource<SourcedSection, Map<String, Object>, JSONSource> {

    public static final @NotNull Gson DEFAULT_GSON = new GsonBuilder()
        .serializeNulls().disableHtmlEscaping().setPrettyPrinting()
        .registerTypeAdapter(
            SourcedSection.class,
            (JsonSerializer<SourcedSection>) (src, t, c) -> c.serialize(src.data())
        ).create();

    protected final @NotNull Gson gson;
    protected @Nullable SourcedSection rootSection;

    protected JSONSource(@NotNull ConfigurationHolder<? extends JSONSource> holder,
                         @NotNull File file, @Nullable String resourcePath) {
        this(holder, file, resourcePath, DEFAULT_GSON);
    }

    protected JSONSource(@NotNull ConfigurationHolder<? extends JSONSource> holder,
                         @NotNull File file, @Nullable String resourcePath, @NotNull Gson gson) {
        super(holder, 0, file, resourcePath);
        this.gson = gson;
        initialize();
    }

    public void initialize() {
        try {
            initializeFile();
            onReload();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected @NotNull JSONSource self() {
        return this;
    }

    @Override
    public @NotNull Map<String, Object> original() {
        return section().data();
    }

    @Override
    public @NotNull SourcedSection section() {
        return Objects.requireNonNull(this.rootSection, "Root section is not initialized");
    }

    @Override
    public void save() throws Exception {
        fileWriter(writer -> gson.toJson(original(), writer));
    }

    @Override
    protected void onReload() throws Exception {
        Map<?, ?> data = fileReader(reader -> gson.fromJson(reader, LinkedHashMap.class));
        this.rootSection = SourcedSection.root(this, data);
        this.lastUpdateMillis = System.currentTimeMillis(); // 更新时间
    }

    @Override
    public String toString() {
        return gson.toJson(original());
    }
}
