package cc.carm.lib.configuration.source.temp;

import cc.carm.lib.configuration.source.ConfigurationFactory;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;

public class TempConfigFactory
    extends ConfigurationFactory<TempSource, ConfigurationHolder<TempSource>, TempConfigFactory> {

    public static @NotNull TempConfigFactory create() {
        return new TempConfigFactory();
    }

    @Override
    protected TempConfigFactory self() {
        return this;
    }

    @Override
    public @NotNull ConfigurationHolder<TempSource> build() {

        return new ConfigurationHolder<TempSource>(this.adapters, this.options, this.metadata, this.initializer) {
            final @NotNull TempSource source = new TempSource(this);

            @Override
            public @NotNull TempSource config() {
                return this.source;
            }
        };
    }


}
