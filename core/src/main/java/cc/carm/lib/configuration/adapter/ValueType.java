package cc.carm.lib.configuration.adapter;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * {@link ValueType} used to get the generic type of the value,
 * It can be used to check if an object is an instance of a specific type,
 * and to cast objects to the correct type.
 * <p>
 * Java's type system is not capable of retaining generic type information at runtime.
 * This class is used to represent a type with its generic parameters.
 * </p>
 */
public abstract class ValueType<T> {

    public static final ValueType<Object> OBJECT = ofPrimitiveType(Object.class);
    public static final ValueType<String> STRING = ofPrimitiveType(String.class);
    public static final ValueType<Integer> INTEGER = ofPrimitiveType(Integer.class);
    public static final ValueType<Integer> INTEGER_TYPE = ofPrimitiveType(int.class);
    public static final ValueType<Long> LONG = ofPrimitiveType(Long.class);
    public static final ValueType<Long> LONG_TYPE = ofPrimitiveType(long.class);
    public static final ValueType<Double> DOUBLE = ofPrimitiveType(Double.class);
    public static final ValueType<Double> DOUBLE_TYPE = ofPrimitiveType(double.class);
    public static final ValueType<Float> FLOAT = ofPrimitiveType(Float.class);
    public static final ValueType<Float> FLOAT_TYPE = ofPrimitiveType(float.class);
    public static final ValueType<Boolean> BOOLEAN = ofPrimitiveType(Boolean.class);
    public static final ValueType<Boolean> BOOLEAN_TYPE = ofPrimitiveType(boolean.class);
    public static final ValueType<Byte> BYTE = ofPrimitiveType(Byte.class);
    public static final ValueType<Byte> BYTE_TYPE = ofPrimitiveType(byte.class);
    public static final ValueType<Short> SHORT = ofPrimitiveType(Short.class);
    public static final ValueType<Short> SHORT_TYPE = ofPrimitiveType(short.class);
    public static final ValueType<Character> CHAR = ofPrimitiveType(Character.class);
    public static final ValueType<Character> CHAR_TYPE = ofPrimitiveType(char.class);

    public static final ValueType<?>[] PRIMITIVE_TYPES = {
        STRING, INTEGER, LONG, DOUBLE, FLOAT, BOOLEAN, BYTE, SHORT, CHAR,
        INTEGER_TYPE, LONG_TYPE, DOUBLE_TYPE, FLOAT_TYPE, BOOLEAN_TYPE, BYTE_TYPE, SHORT_TYPE, CHAR_TYPE
    };

    @SuppressWarnings("unchecked")
    public static <T> ValueType<T> of(@NotNull T value) {
        return of((Class<T>) value.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T> ValueType<T> of(final Type type) {
        if (type == null) throw new NullPointerException("Type cannot be null");
        if (type instanceof Class<?>) { // Try to fast handle primitive types
            Class<?> clazz = (Class<?>) type;
            for (ValueType<?> valueType : PRIMITIVE_TYPES) {
                if (valueType.getRawType() == clazz) {
                    return (ValueType<T>) valueType;
                }
            }
        }
        return new ValueType<T>(type) {
        };
    }

    public static <T> ValueType<T> of(final @NotNull Class<T> clazz) {
        return of((Type) clazz);
    }

    public static <T> ValueType<List<T>> ofList(final @NotNull Class<T> paramType) {
        return of(List.class, paramType);
    }

    public static <T> ValueType<List<T>> ofList(final @NotNull ValueType<T> paramType) {
        return of(List.class, paramType.getType());
    }

    public static <K, V> ValueType<Map<K, V>> ofMap(final @NotNull Class<K> keyType, final @NotNull Class<V> valueType) {
        return of(Map.class, keyType, valueType);
    }

    public static <K, V> ValueType<Map<K, V>> ofMap(final @NotNull ValueType<K> keyType, final @NotNull ValueType<V> valueType) {
        return of(Map.class, keyType.getType(), valueType.getType());
    }

    /**
     * Get the generic type of the complex type.
     *
     * @param rawType The raw type
     * @param types   The type arguments
     * @param <T>     The type
     * @return The {@link ValueType}
     */
    public static <T> ValueType<T> of(final Class<?> rawType, final Type... types) {
        ParameterizedType parameterizedType = new ParameterizedType() {
            @Override
            public @NotNull Type @NotNull [] getActualTypeArguments() {
                return types;
            }

            @Override
            public @NotNull Type getRawType() {
                return rawType;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };
        return of(parameterizedType);
    }

    @ApiStatus.Internal
    private static <T> ValueType<T> ofPrimitiveType(Class<T> clazz) {
        return new ValueType<T>(clazz) {
        };
    }

    private final Type type;

    protected ValueType() {
        this.type = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    private ValueType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    /**
     * Checks if this ValueType is a subtype of the given Class.
     *
     * @param target The target Class to check against
     * @return true if this ValueType is a subtype of the target Class, false otherwise
     */
    public boolean isSubtypeOf(Class<?> target) {
        Class<?> rawType = getRawType();
        return target.isAssignableFrom(rawType);
    }

    /**
     * Checks if this ValueType is a subtype of the given ValueType.
     *
     * @param target The target ValueType to check against
     * @return true if this ValueType is a subtype of the target, false otherwise
     */
    public boolean isSubtypeOf(ValueType<?> target) {
        return target.isSubtypeOf(getRawType());
    }

    /**
     * Checks if the given object is an instance of the type represented by this ValueType.
     *
     * @param obj The object to check
     * @return true if the object is an instance of the type, false otherwise
     */
    public boolean isInstance(Object obj) {
        return obj != null && getRawType().isInstance(obj);
    }


    /**
     * Extracts the raw type from the generic type.
     *
     * @return The raw type of the generic type
     * @throws IllegalStateException if the type is not a Class or ParameterizedType
     */
    @SuppressWarnings("unchecked")
    public Class<T> getRawType() {
        if (type instanceof Class<?>) {
            return (Class<T>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            if (raw instanceof Class<?>) {
                return (Class<T>) raw;
            }
        }
        throw new IllegalStateException("Unsupported type: " + type);
    }

    /**
     * Casts the object to the type represented by this ValueType.
     *
     * @param obj The object to cast
     * @return The object cast to the type represented by this ValueType
     */
    @SuppressWarnings("unchecked")
    public T cast(Object obj) {
        if (!isInstance(obj)) {
            throw new ClassCastException("Cannot cast object " + obj + " to type " + this);
        }
        return (T) obj;
    }

    /**
     * Returns a string representation of the type.
     * Like "{@code java.util.List<java.lang.String>}" or "java.lang.Integer".
     *
     * @return String representation of the type
     */
    @Override
    public String toString() {
        if (type instanceof Class<?>) {
            return ((Class<?>) type).getName();
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type raw = pt.getRawType();
            StringBuilder sb = new StringBuilder();
            sb.append(raw.getClass().getName());
            sb.append('<');
            Type[] args = pt.getActualTypeArguments();
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(args[i].getTypeName());
            }
            sb.append('>');
            return sb.toString();
        }
        return type.getTypeName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ValueType) {
            return Objects.equals(type, ((ValueType<?>) obj).type);
        }
        if (obj instanceof Type) {
            return Objects.equals(type, obj);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }

}
