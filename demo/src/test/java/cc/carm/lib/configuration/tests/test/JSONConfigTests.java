package cc.carm.lib.configuration.tests.test;

import cc.carm.lib.configuration.demo.tests.ConfigurationTest;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.json.JSONConfigFactory;
import org.junit.Test;

import java.io.File;

public class JSONConfigTests {

    protected final ConfigurationHolder<?> holder = JSONConfigFactory
        .from(new File("target"), "config.json")
        .resourcePath("example.json")
        .build();

    @Test
    public void onTest() {
        ConfigurationTest.testDemo(this.holder);
        ConfigurationTest.testInner(this.holder);

        ConfigurationTest.save(this.holder);
    }


}
