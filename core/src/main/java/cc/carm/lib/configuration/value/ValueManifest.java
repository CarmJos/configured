package cc.carm.lib.configuration.value;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.ValueValidator;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetaHolder;
import cc.carm.lib.configuration.source.section.ConfigureSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class ValueManifest<TYPE, UNIT> {

    protected final @NotNull ValueType<TYPE> type;
    protected final @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer;

    protected @Nullable ConfigurationHolder<?> holder;
    protected @Nullable String path; // Section path

    protected @NotNull ValueValidator<? super UNIT> validator;
    protected @NotNull Supplier<@Nullable TYPE> defaultSupplier;

    public ValueManifest(@NotNull ValueType<TYPE> type) {
        this(type, () -> null, ValueValidator.none(), EMPTY_INITIALIZER, null, null);
    }

    public ValueManifest(@NotNull TYPE defaultValue) {
        this(ValueType.of(defaultValue), () -> defaultValue);
    }

    public ValueManifest(@NotNull ValueType<TYPE> type, @NotNull Supplier<@Nullable TYPE> defaultSupplier) {
        this(type, defaultSupplier, ValueValidator.none(), EMPTY_INITIALIZER, null, null);
    }

    public ValueManifest(@NotNull ValueType<TYPE> type,
                         @NotNull Supplier<@Nullable TYPE> defaultSupplier,
                         @NotNull ValueValidator<? super UNIT> validator) {
        this(type, defaultSupplier, validator, EMPTY_INITIALIZER, null, null);
    }

    public ValueManifest(@NotNull ValueType<TYPE> type, @NotNull Supplier<@Nullable TYPE> defaultSupplier,
                         @NotNull ValueValidator<? super UNIT> validator,
                         @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer) {
        this(type, defaultSupplier, validator, initializer, null, null);
    }

    public ValueManifest(@NotNull ValueType<TYPE> type,
                         @NotNull Supplier<@Nullable TYPE> defaultSupplier,
                         @NotNull ValueValidator<? super UNIT> validator,
                         @NotNull BiConsumer<@NotNull ConfigurationHolder<?>, @NotNull String> initializer,
                         @Nullable ConfigurationHolder<?> holder, @Nullable String path) {
        this.type = type;
        this.validator = validator;
        this.initializer = initializer;
        this.defaultSupplier = defaultSupplier;
        this.holder = holder;
        this.path = path;
        initialize();
    }

    protected ValueManifest(@NotNull ValueManifest<TYPE, UNIT> manifest) {
        this(manifest.type, manifest.defaultSupplier, manifest.validator, manifest.initializer, manifest.holder, manifest.path);
    }

    public void initialize(@NotNull ConfigurationHolder<?> holder, @NotNull String path) {
        this.holder = holder;
        this.path = path;
        initialize();
    }

    protected void initialize() {
        if (holder != null && path != null) this.initializer.accept(holder, path);
    }

    public @NotNull ValueType<TYPE> type() {
        return this.type;
    }

    public void holder(@NotNull ConfigurationHolder<?> holder) {
        this.holder = holder;
    }

    public void path(@NotNull String path) {
        this.path = path;
    }

    public @Nullable TYPE defaults() {
        return this.defaultSupplier.get();
    }

    public void defaults(@Nullable TYPE defaultValue) {
        defaults(() -> defaultValue);
    }

    public void defaults(@NotNull Supplier<@Nullable TYPE> defaultValue) {
        this.defaultSupplier = defaultValue;
    }

    public boolean hasDefaults() {
        return defaults() != null;
    }

    public @NotNull ValueValidator<? super UNIT> validator() {
        return this.validator;
    }

    public void validator(@NotNull ValueValidator<? super UNIT> validator) {
        this.validator = validator;
    }

    public void validate(@NotNull ValueValidator<? super UNIT> validator) {
        validator((h, v) -> {
            this.validator.validate(h, v);
            validator.validate(h, v);
        });
    }

    protected UNIT withValidated(@Nullable UNIT value) throws Exception {
        validator.validate(holder(), value);
        return value;
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

    public @NotNull ConfigurationMetaHolder metadata() {
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
