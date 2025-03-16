package cc.carm.lib.configuration.function;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ValueValidator<T> {

    void validate(@NotNull ConfigurationHolder<?> holder, @Nullable T value) throws Exception;

    default ValueValidator<T> compose(ValueValidator<? super T> other) {
        return (holder, value) -> {
            validate(holder, value);
            other.validate(holder, value);
        };
    }

    static <V> ValueValidator<V> none() {
        return (holder, data) -> {
        };
    }

    static <V> ValueValidator<V> nonnull() {
        return nonnull("Value cannot be null");
    }

    static <V> ValueValidator<V> nonnull(String message) {
        return (holder, data) -> {
            if (data == null) throw new IllegalArgumentException(message);
        };
    }

    static <V extends Number> ValueValidator<V> range(V min, V max) {
        return range(min, max, "Value must be in range [" + min + ", " + max + "]");
    }

    static <V extends Number> ValueValidator<V> range(V min, V max, String message) {
        return (holder, data) -> {
            if (data.doubleValue() < min.doubleValue() || data.doubleValue() > max.doubleValue()) {
                throw new IllegalArgumentException(message);
            }
        };
    }


}
