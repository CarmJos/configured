package cc.carm.lib.configuration.function;

import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ConfigExceptionHandler {

    void handle(@NotNull String path, @NotNull Throwable throwable);

    static @NotNull ConfigExceptionHandler silence() {
        return (path, throwable) -> {
        };
    }

    static @NotNull ConfigExceptionHandler print() {
        return (path, throwable) -> {
            System.err.println("Error occurred at path: " + path);
            throwable.printStackTrace();
        };
    }

}
