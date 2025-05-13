package cc.carm.lib.configuration.adapter;

import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ValueAdapterRegistry {

    protected final Set<ValueAdapter<?>> adapters = new HashSet<>();

    public <FROM, TO> void register(@NotNull Class<FROM> from, @NotNull Class<TO> to,
                                    @Nullable DataFunction<FROM, TO> parser,
                                    @Nullable DataFunction<TO, FROM> serializer) {
        register(ValueType.of(from), ValueType.of(to), parser, serializer);
    }

    public <FROM, TO> void register(@NotNull ValueType<FROM> from, @NotNull ValueType<TO> to,
                                    @Nullable DataFunction<FROM, TO> parser,
                                    @Nullable DataFunction<TO, FROM> serializer) {
        ValueAdapter<FROM> fromAdapter = adapterOf(from);
        if (fromAdapter == null) throw new IllegalArgumentException("No adapter for type " + from);
        register(to,
            serializer == null ? null : (provider, type, value) -> fromAdapter.serialize(provider, from, serializer.handle(value)),
            parser == null ? null : (provider, type, data) -> parser.handle(fromAdapter.parse(provider, from, data))
        );
    }

    public void register(@NotNull ValueAdapter<?>... adapter) {
        adapters.addAll(Arrays.asList(adapter));
    }

    public <T> void register(@NotNull Class<T> type, @NotNull ValueSerializer<T> serializer) {
        register(ValueType.of(type), serializer);
    }

    public <T> void register(@NotNull ValueType<T> type, @NotNull ValueSerializer<T> serializer) {
        ValueAdapter<T> existing = adapterOf(type);
        if (existing != null) {
            existing.serializer(serializer);
        } else {
            register(new ValueAdapter<>(type, serializer, null));
        }
    }

    public <T> void register(@NotNull Class<T> type, @NotNull ValueParser<T> deserializer) {
        register(ValueType.of(type), deserializer);
    }

    public <T> void register(@NotNull ValueType<T> type, @NotNull ValueParser<T> deserializer) {
        ValueAdapter<T> existing = adapterOf(type);
        if (existing != null) {
            existing.parser(deserializer);
        } else {
            register(new ValueAdapter<>(type, null, deserializer));
        }
    }

    public <T> void register(@NotNull ValueType<T> type,
                             @Nullable ValueSerializer<T> serializer,
                             @Nullable ValueParser<T> deserializer) {
        if (serializer == null && deserializer == null) return;
        ValueAdapter<T> existing = adapterOf(type);
        if (existing != null) {
            if (serializer != null) existing.serializer(serializer);
            if (deserializer != null) existing.parser(deserializer);
        } else {
            register(new ValueAdapter<>(type, serializer, deserializer));
        }
    }

    public void unregister(@NotNull Class<?> type) {
        unregister(ValueType.of(type));
    }

    public void unregister(@NotNull ValueType<?> type) {
        adapters.removeIf(adapter -> adapter.type().equals(type));
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable ValueAdapter<T> adapterOf(@NotNull ValueType<T> type) {
        ValueAdapter<?> matched = adapters.stream()
            .filter(adapter -> adapter.type().equals(type))
            .findFirst().orElse(null);
        if (matched != null) return (ValueAdapter<T>) matched;

        // If no adapter found, try to find the adapter for the super type
        return (ValueAdapter<T>) adapters.stream()
            .filter(adapter -> adapter.type().isSubtypeOf(type))
            .findFirst().orElse(null);
    }

    public <T> ValueAdapter<T> adapterOf(@NotNull T value) {
        return adapterOf(ValueType.of(value));
    }

    public <T> ValueAdapter<T> adapterOf(@NotNull Class<T> type) {
        return adapterOf(ValueType.of(type));
    }

    public <T> T deserialize(@NotNull ConfigurationHolder<?> holder, @NotNull Class<T> type, @Nullable Object source) throws Exception {
        return deserialize(holder, ValueType.of(type), source);
    }

    public <T> T deserialize(@NotNull ConfigurationHolder<?> holder, @NotNull ValueType<T> type, @Nullable Object source) throws Exception {
        if (source == null) return null; // Null check
        if (type.isInstance(source)) return type.cast(source); // Not required to deserialize
        ValueAdapter<T> adapter = adapterOf(type);
        if (adapter == null) throw new RuntimeException("No adapter for type " + type);
        return adapter.parse(holder, type, source);
    }

    @Nullable
    public <T> Object serialize(@NotNull ConfigurationHolder<?> holder, @Nullable T value) throws Exception {
        if (value == null) return null; // Null check
        ValueType<T> type = ValueType.of(value);
        ValueAdapter<T> adapter = adapterOf(type);
        if (adapter == null) return value; // No adapters, try to return the original value
        return adapter.serialize(holder, type, value);
    }


}
