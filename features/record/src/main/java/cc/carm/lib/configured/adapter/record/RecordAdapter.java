package cc.carm.lib.configured.adapter.record;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueSerializer;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RecordAdapter<T extends Record> extends ValueAdapter<T> {

    public static void register(ConfigurationHolder<?> holder) {
        holder.adapters().register(of(Record.class));
    }

    public static <R extends Record> RecordAdapter<R> of(@NotNull Class<R> type) {
        return of(ValueType.of(type));
    }

    public static <R extends Record> RecordAdapter<R> of(@NotNull ValueType<R> type) {
        return new RecordAdapter<>(type);
    }

    public RecordAdapter(@NotNull ValueType<T> type) {
        super(type, serializer(type), parser(type));
    }

    public static <R extends Record> ValueSerializer<R> serializer(@NotNull ValueType<R> type) {
        return (holder, type1, r) -> toMap(holder, r);
    }

    @SuppressWarnings("unchecked")
    public static <R extends Record> ValueParser<R> parser(@NotNull ValueType<R> type) {
        return (holder, valueType, value) -> {
            if (value instanceof ConfigureSection section) {
                return fromMap(holder, (Class<R>) valueType.getRawType(), section.asMap());
            } else if (value instanceof Map<?, ?> map) {
                return fromMap(holder, (Class<R>) valueType.getRawType(), (Map<String, Object>) map);
            } else return null;
        };
    }

    public static <R extends Record> Map<String, Object> toMap(
        @NotNull ConfigurationHolder<?> holder, @NotNull R record
    ) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        Class<?> recordClass = record.getClass();
        if (!recordClass.isRecord()) {
            throw new IllegalArgumentException("Object is not a record");
        }
        try {
            for (RecordComponent component : recordClass.getRecordComponents()) {
                String name = component.getName();
                Method accessor = component.getAccessor();
                accessor.setAccessible(true);
                Object value = accessor.invoke(record);
                map.put(name, serializeValue(holder, value));
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Failed to convert record to map", e);
        }
        return map;
    }

    public static <R extends Record> R fromMap(
        @NotNull ConfigurationHolder<?> holder,
        @NotNull Class<R> type, @NotNull Map<String, Object> data
    ) throws Exception {
        RecordComponent[] components = type.getRecordComponents();
        Object[] args = new Object[components.length];
        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            args[i] = parseValue(holder, component, data.get(component.getName()));
        }
        return createInstance(type, args);
    }

    @SuppressWarnings("unchecked")
    private static Object parseValue(ConfigurationHolder<?> holder, RecordComponent component, Object value) throws Exception {
        if (value == null) return null;
        if (component.getType().isRecord()) {
            return fromMap(holder, component.getType().asSubclass(Record.class), (Map<String, Object>) value);
        }
        ValueType<?> valueType = ValueType.of(component.getGenericType());
        return holder.deserialize(valueType, value);
    }

    private static Object serializeValue(ConfigurationHolder<?> holder, Object value) throws Exception {
        if (value == null) return null;
        if (value.getClass().isRecord()) {
            return toMap(holder, (Record) value);
        }
        return holder.serialize(value);
    }

    private static <T> T createInstance(Class<T> t, Object[] args) throws Exception {
        Class<?>[] paramTypes = Arrays.stream(t.getRecordComponents())
            .map(RecordComponent::getType).toArray(Class[]::new);
        Constructor<T> constructor = t.getDeclaredConstructor(paramTypes);
        constructor.setAccessible(true); // Make sure the constructor is accessible
        return constructor.newInstance(args);
    }


}
