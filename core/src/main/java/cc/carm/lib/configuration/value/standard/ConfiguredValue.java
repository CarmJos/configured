package cc.carm.lib.configuration.value.standard;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueSerializer;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.value.ConfigValueBuilder;
import cc.carm.lib.configuration.builder.value.SourceValueBuilder;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.impl.CachedConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ConfiguredValue<V> extends CachedConfigValue<V, V> {

    /**
     * Create a new value builder.
     *
     * @param type The type of the value.
     * @param <V>  The type of the value.
     * @return a {@link ConfigValueBuilder} with the specified type.
     */
    public static <V> ConfigValueBuilder<V> builderOf(@NotNull Class<V> type) {
        return new ConfigValueBuilder<>(ValueType.of(type));
    }

    /**
     * Create a new value builder.
     *
     * @param type The type of the value.
     * @param <V>  The type of the value.
     * @return a {@link ConfigValueBuilder} with the specified type.
     */
    public static <V> ConfigValueBuilder<V> builderOf(@NotNull ValueType<V> type) {
        return new ConfigValueBuilder<>(type);
    }

    /**
     * Create a new value builder with the specified {@link ConfigurationHolder#registeredValues()} type.
     *
     * @param registeredType The type of the value.
     * @param <V>            The type of the value.
     * @return a {@link SourceValueBuilder} with the specified registered type.
     * @see ValueAdapter
     */
    public static <V> SourceValueBuilder<V, V> with(@NotNull Class<V> registeredType) {
        return with(ValueType.of(registeredType));
    }

    /**
     * Create a new value builder with the specified {@link ConfigurationHolder#registeredValues()} type.
     *
     * @param registeredType The type of the value.
     * @param <V>            The type of the value.
     * @return a {@link SourceValueBuilder} with the specified registered type.
     * @see ValueAdapter
     */
    public static <V> SourceValueBuilder<V, V> with(@NotNull ValueType<V> registeredType) {
        return new ConfigValueBuilder<>(registeredType).from(registeredType);
    }

    public static <V> ConfiguredValue<V> of(@NotNull V defaults) {
        return of(ValueType.of(defaults), () -> defaults);
    }

    public static <V> ConfiguredValue<V> of(@NotNull Class<V> type) {
        return of(ValueType.of(type), () -> null);
    }

    public static <V> ConfiguredValue<V> of(@NotNull Class<V> type, @NotNull V defaults) {
        return of(ValueType.of(type), () -> defaults);
    }


    public static <V> ConfiguredValue<V> of(@NotNull Class<V> type, @NotNull Supplier<@Nullable V> defaultSupplier) {
        return of(ValueType.of(type), defaultSupplier);
    }

    public static <V> ConfiguredValue<V> of(@NotNull ValueType<V> type) {
        return of(type, () -> null);
    }

    public static <V> ConfiguredValue<V> of(@NotNull ValueType<V> type, @NotNull Supplier<@Nullable V> defaultSupplier) {
        return of(
                new ValueManifest<>(type, defaultSupplier),
                (provider, t, data) -> provider.deserialize(type, data),
                (provider, t, value) -> provider.serialize(value)
        );
    }

    public static <V> ConfiguredValue<V> of(@NotNull ValueManifest<V, V> manifest,
                                            @Nullable ValueParser<V> parser,
                                            @Nullable ValueSerializer<V> serializer) {
        ValueAdapter<V> adapter = new ValueAdapter<>(manifest.type());
        adapter.parser(parser);
        adapter.serializer(serializer);
        return of(manifest, adapter);
    }

    public static <V> ConfiguredValue<V> of(@NotNull ValueManifest<V, V> manifest, @NotNull ValueAdapter<V> adapter) {
        return new ConfiguredValue<>(manifest, adapter);
    }

    protected final @NotNull ValueAdapter<V> adapter;

    public ConfiguredValue(@NotNull ValueManifest<V, V> manifest, @NotNull ValueAdapter<V> adapter) {
        super(manifest);
        this.adapter = adapter;
    }

    /**
     * @return Adapter of this value.
     */
    public @NotNull ValueAdapter<V> adapter() {
        return adapter;
    }

    /**
     * @return Value's parser, parse base object to value.
     */
    public @Nullable ValueParser<V> parser() {
        return parserFor(adapter());
    }

    /**
     * @return Value's serializer, parse value to base object.
     */
    public @Nullable ValueSerializer<V> serializer() {
        return serializerFor(adapter());
    }

    @Override
    public V get() {
        if (!cacheExpired()) return getCachedOrDefault();
        // Data that is outdated and needs to be parsed again.

        try {
            Object data = getData();
            if (data == null) return defaults();

            ValueParser<V> parser = parser();
            if (parser == null) return defaults(); // No parser, return default value.

            // If there are no errors, update the cache and return.
            V parsed = parser.parse(holder(), type(), data);
            return updateCache(withValidated(parsed));
        } catch (Exception e) {
            // There was a validate or parsing error, prompted and returned the default value.
            throwing(e);
            return defaults();
        }

    }

    /**
     * Set the value of the configuration path.
     * Will use {@link #serializer()} to serialize the value.
     *
     * @param value The value to be set
     */
    @Override
    public void set(@Nullable V value) {
        updateCache(value); // Update cache
        if (value == null) {
            setData(null);
            return;
        }

        try {
            ValueSerializer<V> serializer = serializer();
            if (serializer == null) return; // No serializer, do nothing.

            setData(serializer.serialize(holder(), type(), withValidated(value)));
        } catch (Exception e) {
            throwing(e);
        }

    }

}

