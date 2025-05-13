package cc.carm.lib.configuration.builder;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.value.ConfigValue;

public abstract class CommonConfigBuilder<
    TYPE, UNIT,
    RESULT extends ConfigValue<TYPE, UNIT>,
    SELF extends CommonConfigBuilder<TYPE, UNIT, RESULT, SELF>
    > extends AbstractConfigBuilder<TYPE, UNIT, RESULT, ConfigurationHolder<?>, SELF> {

    protected CommonConfigBuilder(ValueType<TYPE> type) {
        super(ConfigurationHolder.class, type);
    }

}
