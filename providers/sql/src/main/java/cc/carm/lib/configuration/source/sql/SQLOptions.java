package cc.carm.lib.configuration.source.sql;

import cc.carm.lib.configuration.source.option.ConfigurationOption;

public interface SQLOptions {

    /**
     * Whether to purge the configuration's in-database data when saving.
     */
    ConfigurationOption<Boolean> PURGE = ConfigurationOption.of(true);

}
