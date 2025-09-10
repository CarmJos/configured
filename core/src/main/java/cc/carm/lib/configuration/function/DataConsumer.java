package cc.carm.lib.configuration.function;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@FunctionalInterface
public interface DataConsumer<T> extends Serializable {

    void accept(@NotNull T data) throws Exception;

    default DataConsumer<T> andThen(DataConsumer<? super T> after) {
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }


}
