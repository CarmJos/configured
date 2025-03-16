package cc.carm.lib.configuration.demo.tests.conf;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.annotation.ValueRange;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

@HeaderComments("service")
public class InstanceConfig implements Configuration {

    @ValueRange(min = 0, max = 100, message = "The value must be between 0 and 100")
    public final ConfiguredValue<Double> STATUS = ConfiguredValue.of(1.0D);

}
