package cc.carm.lib.configuration.source.meta;

import cc.carm.lib.configuration.value.ConfigValue;

public interface StandardMeta {

    /**
     * To mark the {@link ConfigValue} instance of specific path.
     */
    ConfigurationMetadata<ConfigValue<?, ?>> VALUE = ConfigurationMetadata.of();

}
