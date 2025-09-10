package cc.carm.lib.configuration.function;


import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

@FunctionalInterface
public interface ValueComposer<T, U> extends Serializable {

    /**
     * Accept the value and the data, and then compose the value.
     *
     * @param holder The configuration holder
     * @param type   The value type, e.g. {@link java.util.List}, {@link java.util.Map}, etc.
     * @param data   The unit data
     * @throws Exception If an error occurs
     */
    void accept(@NotNull ConfigurationHolder<?> holder, @NotNull T type, @NotNull U data) throws Exception;

    default ValueComposer<T, U> andThen(ValueComposer<? super T, ? super U> after) {
        return (holder, unit, data) -> {
            accept(holder, unit, data);
            after.accept(holder, unit, data);
        };
    }

}


