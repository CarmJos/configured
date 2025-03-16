package cc.carm.lib.configuration.source;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.adapter.ValueAdapterRegistry;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.ConfigExceptionHandler;
import cc.carm.lib.configuration.source.loader.ConfigurationInitializer;
import cc.carm.lib.configuration.source.meta.ConfigurationMetaHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetadata;
import cc.carm.lib.configuration.source.meta.StandardMeta;
import cc.carm.lib.configuration.source.option.ConfigurationOption;
import cc.carm.lib.configuration.source.option.ConfigurationOptionHolder;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import cc.carm.lib.configuration.value.ConfigValue;
import cc.carm.lib.configuration.value.ValueManifest;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public abstract class ConfigurationHolder<SOURCE extends ConfigureSource<?, ?, SOURCE>> {

    protected final @NotNull ValueAdapterRegistry adapters;
    protected final @NotNull ConfigurationOptionHolder options;
    protected final @NotNull Map<String, ConfigurationMetaHolder> metadata;

    protected final @NotNull ConfigurationInitializer initializer;

    protected @NotNull ConfigExceptionHandler exceptionHandler;

    public ConfigurationHolder(@NotNull ValueAdapterRegistry adapters,
                               @NotNull ConfigurationOptionHolder options,
                               @NotNull Map<String, ConfigurationMetaHolder> metadata,
                               @NotNull ConfigurationInitializer initializer) {
        this(adapters, options, metadata, initializer, ConfigExceptionHandler.print());
    }

    public ConfigurationHolder(@NotNull ValueAdapterRegistry adapters,
                               @NotNull ConfigurationOptionHolder options,
                               @NotNull Map<String, ConfigurationMetaHolder> metadata,
                               @NotNull ConfigurationInitializer initializer,
                               @NotNull ConfigExceptionHandler exceptionHandler) {
        this.initializer = initializer;
        this.adapters = adapters;
        this.options = options;
        this.metadata = metadata;
        this.exceptionHandler = exceptionHandler;
    }

    public abstract @NotNull SOURCE config();

    public void reload() throws Exception {
        config().reload();
    }

    public void save() throws Exception {
        config().save();
    }

    public ConfigurationOptionHolder options() {
        return options;
    }

    public <O> @NotNull O option(@NotNull ConfigurationOption<O> option) {
        return options().get(option);
    }

    public @NotNull Map<String, ConfigurationMetaHolder> metadata() {
        return this.metadata;
    }

    public @NotNull ConfigurationMetaHolder metadata(@Nullable String path) {
        return metadata().computeIfAbsent(path, k -> new ConfigurationMetaHolder());
    }

    @NotNull
    @UnmodifiableView
    public <M> Map<String, M> extractMetadata(@NotNull ConfigurationMetadata<M> type) {
        return extractMetadata(type, Objects::nonNull);
    }

    @NotNull
    @UnmodifiableView
    public <M> Map<String, M> extractMetadata(@NotNull ConfigurationMetadata<M> type, @NotNull Predicate<M> filter) {
        Map<String, M> metas = new LinkedHashMap<>();
        for (Map.Entry<String, ConfigurationMetaHolder> entry : this.metadata.entrySet()) {
            M data = entry.getValue().get(type);
            if (filter.test(data)) metas.put(entry.getKey(), data);
        }
        return Collections.unmodifiableMap(metas);
    }

    @NotNull
    @UnmodifiableView
    public Map<String, ConfigValue<?, ?>> registeredValues() {
        return extractMetadata(StandardMeta.VALUE);
    }

    public ValueAdapterRegistry adapters() {
        return this.adapters;
    }

    public ConfigurationInitializer initializer() {
        return initializer;
    }

    @Contract("_,null -> null")
    public <T> T deserialize(@NotNull Class<T> type, @Nullable Object source) throws Exception {
        return adapters().deserialize(this, type, source);
    }

    @Contract("_,null -> null")
    public <T> T deserialize(@NotNull ValueType<T> type, @Nullable Object source) throws Exception {
        return adapters().deserialize(this, type, source);
    }

    @Contract("null -> null")
    public <T> Object serialize(@Nullable T value) throws Exception {
        return adapters().serialize(this, value);
    }

    public void initialize(Class<? extends Configuration> configClass) {
        try {
            initializer.initialize(this, configClass);
        } catch (Exception e) {
            throwing(configClass.getName(), e);
        }
    }

    public void initialize(@NotNull Configuration config) {
        try {
            initializer.initialize(this, config);
        } catch (Exception e) {
            throwing(config.getClass().getName(), e);
        }
    }

    public void initialize(@NotNull ValueManifest<?, ?> value) {
        value.holder(this);
    }

    public void throwing(@NotNull String path, @NotNull Throwable e) {
        this.exceptionHandler.handle(path, e);
    }

    public void exceptionally(@NotNull ConfigExceptionHandler handler) {
        this.exceptionHandler = handler;
    }

}
