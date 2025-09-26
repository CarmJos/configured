package cc.carm.lib.configuration.adapter;

import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ValueAdapterRegistry {

    protected final Set<ValueAdapter<?>> adapters = new HashSet<>();
    protected final Map<ValueType<?>, ValueAdapter<?>> adapterCache = new HashMap<>();

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
        adapterCache.clear();
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
        adapterCache.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable ValueAdapter<T> adapterOf(@NotNull ValueType<T> type) {
        if (adapterCache.containsKey(type)) {
            return (ValueAdapter<T>) adapterCache.get(type);
        }

        for (ValueAdapter<?> adapter : adapters) {
            if (adapter.type().equals(type)) {
                adapterCache.put(type, adapter);
                return (ValueAdapter<T>) adapter;
            }
        }

        for (ValueAdapter<?> adapter : adapters) {
            if (adapter.type().isSubtypeOf(type)) {
                adapterCache.put(type, adapter);
                return (ValueAdapter<T>) adapter;
            }
        }

        adapterCache.put(type, null);
        return null;
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
        if (source == null) return null;

        Type typeInstance = type.getType();
        if (!(typeInstance instanceof ParameterizedType) && type.isInstance(source)) {
            return type.cast(source);
        }

        ValueAdapter<T> adapter = adapterOf(type);
        if (adapter != null) {
            return adapter.parse(holder, type, source);
        }

        return deserializeWithoutAdapter(holder, type, source, typeInstance);
    }

    private <T> T deserializeWithoutAdapter(@NotNull ConfigurationHolder<?> holder, @NotNull ValueType<T> type,
                                            @NotNull Object source, @NotNull Type typeInstance) throws Exception {
        Class<?> rawType = type.getRawType();

        if (rawType.isArray()) {
            return deserializeArray(holder, type, source, rawType);
        }

        if (typeInstance instanceof ParameterizedType) {
            return deserializeParameterized(holder, type, source, (ParameterizedType) typeInstance);
        }

        throw new RuntimeException("No adapter for type " + type);
    }

    private <T> T deserializeArray(@NotNull ConfigurationHolder<?> holder, @NotNull ValueType<T> type,
                                   @NotNull Object source, @NotNull Class<?> rawType) throws Exception {
        List<?> list;
        if (source instanceof List<?>) {
            list = (List<?>) source;
        } else {
            // For non-list sources, treat as single element array
            list = Collections.singletonList(source);
        }

        int size = list.size();
        if (size == 0) {
            return type.cast(Array.newInstance(rawType.getComponentType(), 0));
        }

        Class<?> componentType = rawType.getComponentType();
        Object[] array = (Object[]) Array.newInstance(componentType, size);
        for (int i = 0; i < size; i++) {
            array[i] = deserialize(holder, componentType, list.get(i));
        }
        return type.cast(array);
    }

    @SuppressWarnings("unchecked")
    private <T> T deserializeParameterized(@NotNull ConfigurationHolder<?> holder, @NotNull ValueType<T> type,
                                           @NotNull Object source, @NotNull ParameterizedType pt) throws Exception {
        Type rawType = pt.getRawType();
        Type[] typeArgs = pt.getActualTypeArguments();

        if (rawType == List.class || rawType == Collection.class || rawType == ArrayList.class) {
            return (T) deserializeCollection(holder, source, typeArgs[0], ArrayList::new);
        }

        if (rawType == Set.class || rawType == HashSet.class) {
            return (T) deserializeCollection(holder, source, typeArgs[0], HashSet::new);
        }

        if (rawType == Map.class || rawType == LinkedHashMap.class) {
            return (T) deserializeMap(holder, source, typeArgs[0], typeArgs[1]);
        }

        throw new RuntimeException("No adapter for parameterized type " + type);
    }

    private Collection<?> deserializeCollection(@NotNull ConfigurationHolder<?> holder, @NotNull Object source,
                                                @NotNull Type elementType, @NotNull java.util.function.Supplier<Collection<Object>> collectionFactory) throws Exception {
        ValueType<?> elementValueType = ValueType.of(elementType);
        List<?> sourceList = deserializeList(holder, elementValueType, source);

        if (sourceList.isEmpty()) {
            return collectionFactory.get();
        }

        Collection<Object> result = collectionFactory.get();
        if (result instanceof ArrayList) {
            ((ArrayList<Object>) result).ensureCapacity(sourceList.size());
        }

        for (Object item : sourceList) {
            Object deserializedItem = deserialize(holder, elementValueType, item);
            if (deserializedItem != null) {
                result.add(deserializedItem);
            }
        }
        return result;
    }

    private Map<Object, Object> deserializeMap(@NotNull ConfigurationHolder<?> holder, @NotNull Object source,
                                               @NotNull Type keyType, @NotNull Type valueType) throws Exception {
        Map<?, ?> sourceMap;
        if (source instanceof Map<?, ?>) {
            sourceMap = (Map<?, ?>) source;
        } else if (source instanceof ConfigureSection) {
            sourceMap = ((ConfigureSection) source).asMap();
        } else {
            throw new IllegalArgumentException("Cannot deserialize to Map from " + source.getClass());
        }

        int mapSize = sourceMap.size();
        if (mapSize == 0) {
            return new LinkedHashMap<>();
        }

        ValueType<?> keyValueType = ValueType.of(keyType);
        ValueType<?> valueValueType = ValueType.of(valueType);
        Map<Object, Object> resultMap = new LinkedHashMap<>(mapSize);

        for (Map.Entry<?, ?> entry : sourceMap.entrySet()) {
            Object key = deserialize(holder, keyValueType, entry.getKey());
            Object value = deserialize(holder, valueValueType, entry.getValue());
            resultMap.put(key, value);
        }
        return resultMap;
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
