package cc.carm.lib.configuration.value.text.tests;


import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.json.JSONConfigFactory;
import cc.carm.lib.configuration.value.text.tests.conf.AppMessages;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class ConfigTest {

    public static final String[] WEBSITES = new String[]{
            "https://carm.cc",
            "https://www.baidu.com",
            "https://www.google.com"
    };

    @Test
    public void test() {

        ConfigurationHolder<?> holder = JSONConfigFactory.from(new File("target/messages.json")).build();

        holder.initialize(AppMessages.class);

        System.out.println("--------------------------");

        List<String> str = AppMessages.WELCOME.prepare()
                .placeholders("Carm")
                .insert("guidance")
                .insert("websites", WEBSITES)
                .compile(System.out);

        System.out.println("--------------------------");

        AppMessages.NOT_AVAILABLE.sendTo(System.out);

        System.out.println("--------------------------");

        AppMessages.NO_PERMISSION.sendTo(System.out);

        System.out.println("--------------------------");

    }


}
