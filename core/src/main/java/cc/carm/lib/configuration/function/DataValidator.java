package cc.carm.lib.configuration.function;

import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface DataValidator<T> {

    void validate(@Nullable T value) throws Exception;

    default DataValidator<T> compose(DataValidator<? super T> other) {
        return value -> {
            validate(value);
            other.validate(value);
        };
    }

}
