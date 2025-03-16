package cc.carm.lib.configuration.source.loader;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ConfigInitializeHandler<T, V> {

    static <T, V> ConfigInitializeHandler<T, V> start() {
        return (provider, path, value, instance) -> {
        };
    }

    void whenInitialize(@NotNull ConfigurationHolder<?> holder, @Nullable String path,
                        @NotNull T value, @Nullable V instance) throws Exception;

    default ConfigInitializeHandler<T, V> andThen(ConfigInitializeHandler<T, V> after) {
        return (provider, path, value, instance) -> {
            whenInitialize(provider, path, value, instance);
            after.whenInitialize(provider, path, value, instance);
        };
    }

    default ConfigInitializeHandler<T, V> compose(ConfigInitializeHandler<T, V> before) {
        return (provider, path, value, instance) -> {
            before.whenInitialize(provider, path, value, instance);
            whenInitialize(provider, path, value, instance);
        };
    }


}
