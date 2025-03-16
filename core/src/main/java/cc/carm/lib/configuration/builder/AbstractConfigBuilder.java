package cc.carm.lib.configuration.builder;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.DataValidator;
import cc.carm.lib.configuration.function.ValueValidator;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetaHolder;
import cc.carm.lib.configuration.source.meta.ConfigurationMetadata;
import cc.carm.lib.configuration.value.ConfigValue;
import cc.carm.lib.configuration.value.ValueManifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

@NotNullByDefault
public abstract class AbstractConfigBuilder<
        TYPE, UNIT, RESULT extends ConfigValue<TYPE, UNIT>, HOLDER extends ConfigurationHolder<?>,
        SELF extends AbstractConfigBuilder<TYPE, UNIT, RESULT, HOLDER, SELF>
        > {

    protected final Class<? super HOLDER> providerClass;
    protected final ValueType<TYPE> type;

    protected @Nullable HOLDER holder;
    protected @Nullable String path;

    protected @NotNull ValueValidator<UNIT> valueValidator = ValueValidator.none();
    protected @NotNull Supplier<@Nullable TYPE> defaultValueSupplier = () -> null;
    protected @NotNull BiConsumer<ConfigurationHolder<?>, String> initializer = (h, p) -> {
    };

    protected AbstractConfigBuilder(Class<? super HOLDER> providerClass, ValueType<TYPE> type) {
        this.providerClass = providerClass;
        this.type = type;
    }

    public @NotNull ValueType<TYPE> type() {
        return type;
    }

    protected abstract SELF self();

    public abstract @NotNull RESULT build();

    public SELF holder(@Nullable HOLDER holder) {
        this.holder = holder;
        return self();
    }

    public SELF path(@Nullable String path) {
        this.path = path;
        return self();
    }

    /**
     * Set the {@link ValueValidator} for the value.
     *
     * @param validator The validator to set.
     * @return this builder
     */
    public SELF validator(@NotNull ValueValidator<UNIT> validator) {
        this.valueValidator = validator;
        return self();
    }

    /**
     * Set the {@link DataValidator} for the value.
     *
     * @param validator The validator to set.
     * @return this builder
     */
    public SELF validator(@NotNull DataValidator<? super UNIT> validator) {
        return validator((h, value) -> validator.validate(value));
    }

    /**
     * Validate the value with the specified condition.
     *
     * @param validator The validator to append.
     * @return this builder
     */
    public SELF validate(@NotNull ValueValidator<? super UNIT> validator) {
        return validator(this.valueValidator.and(validator));
    }

    /**
     * Validate the value with the specified condition.
     *
     * @param validator The validator to append.
     * @return this builder
     */
    public SELF validate(@NotNull DataValidator<? super UNIT> validator) {
        return validate((h, value) -> validator.validate(value));
    }

    /**
     * Validate the value with the specified condition.
     *
     * @param condition The condition to check, if the condition is false, an exception will be thrown.
     * @param exception The exception to throw if the condition is false.
     * @return this builder
     */
    public SELF validate(@NotNull Predicate<? super UNIT> condition, @NotNull Exception exception) {
        return validate((h, value) -> {
            if (!condition.test(value)) throw exception;
        });
    }

    /**
     * Validate the value with the specified condition.
     *
     * @param condition The condition to check, if the condition is false, an exception will be thrown.
     * @param msg       The message to throw if the condition is false.
     * @return this builder
     */
    public SELF validate(@NotNull Predicate<? super UNIT> condition, @NotNull String msg) {
        return validate((h, value) -> {
            if (!condition.test(value)) throw new IllegalArgumentException(msg);
        });
    }

    public SELF initializer(@NotNull BiConsumer<ConfigurationHolder<?>, String> initializer) {
        this.initializer = initializer;
        return self();
    }

    public SELF append(@NotNull BiConsumer<ConfigurationHolder<?>, String> initializer) {
        return initializer(initializer.andThen(initializer));
    }

    public SELF append(@NotNull Consumer<ConfigurationHolder<?>> initializer) {
        return append((provider, valuePath) -> initializer.accept(provider));
    }

    public SELF defaults(@Nullable TYPE defaultValue) {
        return defaults(() -> defaultValue);
    }

    public SELF defaults(@NotNull Supplier<@Nullable TYPE> supplier) {
        this.defaultValueSupplier = supplier;
        return self();
    }

    public SELF meta(@NotNull Consumer<@NotNull ConfigurationMetaHolder> metaConsumer) {
        return append((h, p) -> metaConsumer.accept(h.metadata(p)));
    }

    public <M> SELF meta(@NotNull ConfigurationMetadata<M> type, @Nullable M value) {
        return meta(h -> h.set(type, value));
    }

    protected @NotNull ValueManifest<TYPE, UNIT> buildManifest() {
        return new ValueManifest<>(
                type(), this.defaultValueSupplier, this.valueValidator,
                this.initializer, this.holder, this.path
        );
    }

}
