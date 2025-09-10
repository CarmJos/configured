package cc.carm.lib.configuration.function;


import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Objects;

@FunctionalInterface
public interface ValueHandler<T, R> extends Serializable {

    @Nullable R handle(@NotNull ConfigurationHolder<?> holder, @NotNull T data) throws Exception;

    default <V> ValueHandler<T, V> andThen(@NotNull ValueHandler<R, V> after) {
        Objects.requireNonNull(after);
        return ((provider, data) -> {
            R result = handle(provider, data);
            if (result == null) return null;
            else return after.handle(provider, result);
        });
    }

    default <V> ValueHandler<V, R> compose(@NotNull ValueHandler<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return ((provider, data) -> {
            T result = before.handle(provider, data);
            if (result == null) return null;
            return handle(provider, result);
        });
    }

    default <V> ValueHandler<V, R> compose(@NotNull DataFunction<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return ((provider, data) -> {
            T result = before.handle(data);
            return handle(provider, result);
        });
    }

    @Contract(pure = true)
    static <T> @NotNull ValueHandler<T, T> identity() {
        return (provider, input) -> input;
    }

    @Contract(pure = true)
    static <T> @NotNull ValueHandler<T, Object> toObject() {
        return ConfigurationHolder::serialize;
    }

    @Contract(pure = true)
    static <T> @NotNull ValueHandler<T, String> stringValue() {
        return (provider, input) -> String.valueOf(input);
    }

    @Contract(pure = true)
    static <O, T> @NotNull ValueHandler<O, T> deserialize(ValueType<T> to) {
        return (provider, input) -> provider.deserialize(to, input);
    }

    @Contract(pure = true)
    static <T, V> @NotNull ValueHandler<T, V> required() {
        return (provider, input) -> {
            throw new IllegalArgumentException("Please specify the value parser.");
        };
    }

    @Contract(pure = true)
    static <T, V> @NotNull ValueHandler<T, V> required(ValueType<V> type) {
        return (provider, input) -> {
            if (type.isInstance(input)) return type.cast(input); // Simple cast
            throw new IllegalArgumentException("Please specify the value parser.");
        };
    }
}


