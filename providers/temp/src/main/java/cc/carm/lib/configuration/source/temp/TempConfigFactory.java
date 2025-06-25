package cc.carm.lib.configuration.source.temp;

import cc.carm.lib.configuration.source.ConfigurationFactory;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TempConfigFactory
        extends ConfigurationFactory<TempSource, ConfigurationHolder<TempSource>, TempConfigFactory> {

    public static @NotNull TempConfigFactory create() {
        return new TempConfigFactory();
    }

    protected Map<String, Object> defaults = new LinkedHashMap<>();

    public TempConfigFactory defaults(@NotNull Map<String, Object> defaults) {
        this.defaults = defaults;
        return this;
    }

    public TempConfigFactory defaults(Supplier<Map<String, Object>> defaultsSupplier) {
        return defaults(defaultsSupplier.get());
    }

    public TempConfigFactory defaults(@NotNull Consumer<Map<String, Object>> defaultsConsumer) {
        return defaults(() -> {
            Map<String, Object> defaults = new LinkedHashMap<>();
            defaultsConsumer.accept(defaults);
            return defaults;
        });
    }

    @Override
    protected TempConfigFactory self() {
        return this;
    }

    @Override
    public @NotNull ConfigurationHolder<TempSource> build() {

        return new ConfigurationHolder<TempSource>(this.adapters, this.options, this.metadata, this.initializer) {
            final @NotNull TempSource source = new TempSource(this, defaults);

            @Override
            public @NotNull TempSource config() {
                return this.source;
            }
        };
    }


}
