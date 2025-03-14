package cc.carm.lib.configuration.value;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetaHolder;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ValueManifest<T> {

    protected final @NotNull ValueType<T> type;
    protected final @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer;

    protected @Nullable ConfigurationHolder<?> holder;
    protected @Nullable String path; // Section path

    protected @NotNull Supplier<@Nullable T> defaultSupplier;

    public ValueManifest(@NotNull ValueType<T> type) {
        this(type, () -> null, EMPTY_INITIALIZER, null, null);
    }

    public ValueManifest(@NotNull T defaultValue) {
        this(ValueType.of(defaultValue), () -> defaultValue);
    }

    public ValueManifest(@NotNull ValueType<T> type, @NotNull Supplier<@Nullable T> defaultSupplier) {
        this(type, defaultSupplier, EMPTY_INITIALIZER, null, null);
    }

    public ValueManifest(@NotNull ValueType<T> type, @NotNull Supplier<@Nullable T> defaultSupplier,
                         @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer) {
        this(type, defaultSupplier, initializer, null, null);
    }

    public ValueManifest(@NotNull ValueType<T> type, @NotNull Supplier<@Nullable T> defaultSupplier,
                         @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer,
                         @Nullable ConfigurationHolder<?> holder, @Nullable String path) {
        this.type = type;
        this.initializer = initializer;
        this.defaultSupplier = defaultSupplier;
        this.holder = holder;
        this.path = path;
        initialize();
    }

    protected ValueManifest(@NotNull ValueManifest<T> manifest) {
        this(manifest.type, manifest.defaultSupplier, manifest.initializer, manifest.holder, manifest.path);
    }

    public void initialize(@NotNull ConfigurationHolder<?> holder, @NotNull String path) {
        this.holder = holder;
        this.path = path;
        initialize();
    }

    protected void initialize() {
        if (holder != null && path != null) this.initializer.accept(holder, path);
    }

    public @NotNull ValueType<T> type() {
        return this.type;
    }

    public void holder(@NotNull ConfigurationHolder<?> holder) {
        this.holder = holder;
    }

    public void path(@NotNull String path) {
        this.path = path;
    }

    public @Nullable T defaults() {
        return this.defaultSupplier.get();
    }

    public void defaults(@Nullable T defaultValue) {
        defaults(() -> defaultValue);
    }

    public void defaults(@NotNull Supplier<@Nullable T> defaultValue) {
        this.defaultSupplier = defaultValue;
    }

    public boolean hasDefaults() {
        return this.defaultSupplier.get() != null;
    }

    public @NotNull String path() {
        if (path != null) return path;
        else throw new IllegalStateException("No section path provided for Value(" + type() + ").");
    }

    public @NotNull ConfigurationHolder<?> holder() {
        if (this.holder != null) return this.holder;
        throw new IllegalStateException("Value(" + type() + ") does not have a provider.");
    }

    public @NotNull ConfigureSource<?, ?, ?> config() {
        return holder().config();
    }

    public ConfigurationMetaHolder metadata() {
        return holder().metadata(path());
    }

    @ApiStatus.Internal
    protected @Nullable Object getData() {
        return config().get(path());
    }

    @ApiStatus.Internal
    protected void setData(@Nullable Object value) {
        config().set(path(), value);
    }

    private static final @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> EMPTY_INITIALIZER = (provider, valuePath) -> {
    };

}
