package cc.carm.lib.configuration.source.hocon;

import cc.carm.lib.configuration.commentable.Commentable;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.file.FileConfigSource;
import cc.carm.lib.configuration.source.section.SourcedSection;
import com.typesafe.config.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class HOCONSource
    extends FileConfigSource<SourcedSection, Map<String, Object>, HOCONSource> {
    protected @Nullable SourcedSection rootSection;

    protected HOCONSource(
        @NotNull ConfigurationHolder<? extends HOCONSource> holder,
        @NotNull File file, @Nullable String resourcePath
    ) {
        super(holder, 0, file, resourcePath);

        this.initialize();
    }

    public void initialize() {
        try {
            this.initializeFile();
            this.onReload();
        } catch (Exception e) {
            //noinspection CallToPrintStackTrace
            e.printStackTrace();
        }
    }

    @Override
    protected @NotNull HOCONSource self() {
        return this;
    }

    @Override
    public @NotNull Map<String, Object> original() {
        return this.section().data();
    }

    @Override
    public @NotNull SourcedSection section() {
        return Objects.requireNonNull(this.rootSection, "Root section is not initialized.");
    }

    public @NotNull String saveToString() {
        // identity: 创建新的空 typesafe config
        // accumulator: 将 Section 中的信息为 typesafe config 添加并返回
        // combiner: 合并两个配置文件
        Config config = this.getValues(true).entrySet().stream().reduce(
            ConfigFactory.empty(),
            (cfg, entry) -> {
                String key = entry.getKey(); // 源数据 key
                Object value = entry.getValue(); // 源数据 value

                ConfigValue result; // 最终转换为 typesafe 的 ConfigValue 类型
                if (value == null || value instanceof Boolean || value instanceof String || value instanceof Number) {
                    result = ConfigValueFactory.fromAnyRef(value); // 原始数据类型
                } else if (value instanceof Iterator) {
                    result = ConfigValueFactory.fromIterable((Iterable<?>) value);
                } else if (value instanceof Map) {
                    //noinspection unchecked
                    result = ConfigValueFactory.fromMap((Map<String, ?>) value);
                } else {
                    result = ConfigValueFactory.fromAnyRef(String.valueOf(value));
                }
                List<String> headerComments = HOCONSource.this.getHeaderComments(key); // 获取其注释
                result = result.withOrigin(result.origin().withComments(headerComments)); // 赋予其注释
                return cfg.withValue(key, result); // 将其添加到根 config 中
            },
            Config::withFallback
        );
        return config.root().render(
            ConfigRenderOptions.defaults()
                .setJson(false)
                .setOriginComments(false)
        );
    }

    @Override
    public void save() throws Exception {
        this.fileWriter(w -> w.write(HOCONSource.this.saveToString()));
    }

    @Override
    protected void onReload() throws Exception {
        this.rootSection = this.fileReadString(this::loadFromString);
    }

    protected @NotNull SourcedSection loadFromString(@NotNull String data) {
        ConfigObject config = ConfigFactory.parseString(data).root();
        return SourcedSection.root(this, config.unwrapped());
    }

    public @Nullable List<String> getHeaderComments(@Nullable String key) {
        return Commentable.getHeaderComments(holder(), key);
    }
}
