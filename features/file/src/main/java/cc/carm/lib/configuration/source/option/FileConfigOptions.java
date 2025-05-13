package cc.carm.lib.configuration.source.option;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public interface FileConfigOptions {

    /**
     * The charset of the file.
     */
    ConfigurationOption<Charset> CHARSET = ConfigurationOption.of(StandardCharsets.UTF_8);

    /**
     * Whether to copy files from resource if exists.
     */
    ConfigurationOption<Boolean> COPY_DEFAULTS = ConfigurationOption.of(true);

}
