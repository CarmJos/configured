package cc.carm.lib.configuration.adapter;

import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

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
        if (!(type.getType() instanceof ParameterizedType) && type.isInstance(source)) {
            return type.cast(source); // Not required to deserialize
        }

        ValueAdapter<T> adapter = adapterOf(type); // Try to find an existed adapter for the type
        if (adapter != null) {
            return adapter.parse(holder, type, source);
        } // If no adapter found, we will try to handle the type manually

        if (type.getRawType().isArray()) { // For arrays.
            List<?> list = deserializeList(holder, type, source);
            Object[] array = (Object[]) java.lang.reflect.Array.newInstance(type.getRawType().getComponentType(), list.size());
            for (int i = 0; i < list.size(); i++) {
                array[i] = deserialize(holder, type.getRawType().getComponentType(), list.get(i));
            }
            return type.cast(array);
        } else if (type.getType() instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type.getType();
            Type rawType = pt.getRawType();
            Type[] typeArgs = pt.getActualTypeArguments();
            if (rawType == List.class || rawType == Collection.class || rawType == ArrayList.class) {
                return type.cast(new ArrayList<>(deserializeList(holder, ValueType.of(typeArgs[0]), source)));
            } else if (rawType == Set.class || rawType == HashSet.class) {
                return type.cast(new HashSet<>(deserializeList(holder, ValueType.of(typeArgs[0]), source)));
            } else if (rawType == Map.class || rawType == LinkedHashMap.class) {
                Map<?, ?> map;
                if (source instanceof Map<?, ?>) {
                    map = (Map<?, ?>) source;
                } else if (source instanceof ConfigureSection) {
                    map = ((ConfigureSection) source).asMap();
                } else {
                    throw new IllegalArgumentException("Cannot deserialize to Map from " + source.getClass());
                }
                Map<Object, Object> resultMap = new LinkedHashMap<>(map.size());
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    Object key = deserialize(holder, ValueType.of(typeArgs[0]), entry.getKey());
                    Object value = deserialize(holder, ValueType.of(typeArgs[1]), entry.getValue());
                    resultMap.put(key, value);
                }
                return type.cast(resultMap);
            }
        }
        throw new RuntimeException("No adapter for type " + type);
    }

    @Nullable
    public <T> Object serialize(@NotNull ConfigurationHolder<?> holder, @Nullable T value) throws Exception {
        if (value == null) return null; // Null check

        ValueType<T> type = ValueType.of(value);
        ValueAdapter<T> adapter = adapterOf(type);
        if (adapter != null) return adapter.serialize(holder, type, value);

        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            List<Object> serializedList = new ArrayList<>(array.length);
            for (Object item : array) {
                serializedList.add(serialize(holder, item));
            }
            return serializedList;
        } else if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            List<Object> serializedList = new ArrayList<>(collection.size());
            for (Object item : collection) {
                serializedList.add(serialize(holder, item));
            }
            return serializedList;
        } else if (value instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) value;
            Map<Object, Object> serializedMap = new LinkedHashMap<>(map.size());
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = serialize(holder, entry.getKey());
                Object val = serialize(holder, entry.getValue());
                serializedMap.put(key, val);
            }
            return serializedMap;
        }

        return value; // No adapters, and cannot handle, try to return the original value
    }

    protected <T> List<T> deserializeList(@NotNull ConfigurationHolder<?> holder,
                                          @NotNull ValueType<T> type, @Nullable Object source) throws Exception {
        if (source == null) return Collections.emptyList(); // Null check
        if (source instanceof List<?>) {
            List<?> list = (List<?>) source;
            List<T> result = new ArrayList<>(list.size());
            for (Object item : list) {
                T deserializedItem = deserialize(holder, type, item);
                if (deserializedItem != null) {
                    result.add(deserializedItem);
                }
            }
            return result;
        } else { // Maybe singleton? Let's try to deserialize it as a single element list
            T deserializedItem = deserialize(holder, type, source);
            if (deserializedItem != null) {
                return Collections.singletonList(deserializedItem);
            } else return Collections.emptyList();
        }
    }

}
