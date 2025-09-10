package cc.carm.lib.configuration.function;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

@FunctionalInterface
public interface DataValidator<T> extends Serializable {

    void validate(@Nullable T value) throws Exception;

    default DataValidator<T> compose(DataValidator<? super T> other) {
        return value -> {
            validate(value);
            other.validate(value);
        };
    }

}
