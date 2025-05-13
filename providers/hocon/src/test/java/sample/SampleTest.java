package sample;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.hocon.HOCONConfigFactory;
import org.junit.Test;

public class SampleTest {

    @Test
    public void test() {
        // 1. Make a configuration provider from a file.
        ConfigurationHolder<?> holder = HOCONConfigFactory.from("target/config.conf")
            .resourcePath("configs/sample.conf")
            .build();

        // 2. Initialize the configuration classes or instances.
        holder.initialize(SampleConfig.class);
        // 3. Enjoy using the configuration!
        System.out.println("Enabled? -> " + SampleConfig.ENABLED.resolve());
        SampleConfig.ENABLED.set(false);
        System.out.println("And now? -> " + SampleConfig.ENABLED.resolve());
        // p.s. Changes not save so enable value will still be true in the next run.

        System.out.println("Your name is " + SampleConfig.INFO.NAME.resolve() + " (age=" + SampleConfig.INFO.AGE.resolve() + ")!");
    }

}
