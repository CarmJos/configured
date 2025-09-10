package cc.carm.lib.configuration.function;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

@FunctionalInterface
public interface DataFunction<T, R> extends Serializable {

    @NotNull R handle(@NotNull T data) throws Exception;

    default <V> @NotNull DataFunction<T, V> andThen(@NotNull DataFunction<? super R, V> after) {
        Objects.requireNonNull(after);
        return data -> after.handle(handle(data));
    }

    @Contract(pure = true)
    static <T> @NotNull DataFunction<T, T> identity() {
        return input -> input;
    }

    @Contract(pure = true)
    static <T> @NotNull DataFunction<T, T> identity(Class<T> type) {
        return input -> input;
    }

    @Contract(pure = true)
    static <T, V> @NotNull DataFunction<T, V> required() {
        return input -> {
            throw new IllegalArgumentException("Please specify the value parser.");
        };
    }

    @Contract(pure = true)
    static <T> @NotNull DataFunction<T, Object> toObject() {
        return input -> input;
    }

    @Contract(pure = true)
    static <V> @NotNull DataFunction<Object, V> castObject(Class<V> valueClass) {
        return input -> {
            if (valueClass.isInstance(input)) return valueClass.cast(input);
            else throw new IllegalArgumentException("Cannot cast value to " + valueClass.getName());
        };
    }

    @Contract(pure = true)
    static <V> @NotNull DataFunction<String, V> castFromString(Class<V> valueClass) {
        return input -> {
            if (valueClass.isInstance(input)) return valueClass.cast(input);
            else throw new IllegalArgumentException("Cannot cast string to " + valueClass.getName());
        };
    }

    @Contract(pure = true)
    static <T> @NotNull DataFunction<T, String> castToString() {
        return input -> {
            if (input instanceof String) return (String) input;
            else if (input instanceof Enum<?>) return ((Enum<?>) input).name();
            else return input.toString();
        };
    }

    @Contract(pure = true)
    static <V> @NotNull DataFunction<String, V> parseString(Class<V> valueClass) {
        return input -> {
            if (valueClass.isInstance(input)) return valueClass.cast(input);
            else throw new IllegalArgumentException("Cannot cast string to " + valueClass.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Integer> intValue() {
        return input -> {
            if (input instanceof Integer) {
                return (Integer) input;
            } else if (input instanceof Number) {
                return ((Number) input).intValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Integer.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Short> shortValue() {
        return input -> {
            if (input instanceof Short) {
                return (Short) input;
            } else if (input instanceof Number) {
                return ((Number) input).shortValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Short.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Double> doubleValue() {
        return input -> {
            if (input instanceof Double) {
                return (Double) input;
            } else if (input instanceof Number) {
                return ((Number) input).doubleValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Double.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Byte> byteValue() {
        return input -> {
            if (input instanceof Byte) {
                return (Byte) input;
            } else if (input instanceof Number) {
                return ((Number) input).byteValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Byte.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Float> floatValue() {
        return input -> {
            if (input instanceof Float) {
                return (Float) input;
            } else if (input instanceof Number) {
                return ((Number) input).floatValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Float.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Long> longValue() {
        return input -> {
            if (input instanceof Long) {
                return (Long) input;
            } else if (input instanceof Number) {
                return ((Number) input).longValue();
            } else throw new IllegalArgumentException("Cannot cast value to " + Long.class.getName());
        };
    }

    @Contract(pure = true)
    static @NotNull DataFunction<Object, Boolean> booleanValue() {
        return input -> {
            if (input instanceof Boolean) {
                return (Boolean) input;
            } else if (input instanceof String) {
                String s = (String) input;
                return Boolean.parseBoolean(s) || "yes".equalsIgnoreCase(s);
            } else if (input instanceof Integer) {
                return (Integer) input == 1;
            } else throw new IllegalArgumentException("Cannot cast value to " + Boolean.class.getName());
        };
    }


}


