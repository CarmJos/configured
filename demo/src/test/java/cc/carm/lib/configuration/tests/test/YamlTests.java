package cc.carm.lib.configuration.tests.test;

import cc.carm.lib.configuration.commentable.CommentableMeta;
import cc.carm.lib.configuration.demo.tests.ConfigurationTest;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.yaml.YAMLConfigFactory;
import cc.carm.lib.configuration.source.yaml.YAMLSource;
import cc.carm.lib.configuration.validators.Validators;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class YamlTests {

    @Test
    public void test() throws Exception {

        ConfigurationHolder<YAMLSource> holder = YAMLConfigFactory.from("target/tests.yml")
            .resourcePath("configs/sample.yml").build();

        Validators.activate(holder);

        ConfigurationTest.testDemo(holder);
        ConfigurationTest.testInner(holder);

        Map<String, List<String>> headers = holder.extractMetadata(CommentableMeta.HEADER);
        System.out.println("Header comments: ");
        headers.forEach((k, v) -> {
            if (v.isEmpty()) return;
            System.out.println("- " + k + ": ");
            v.forEach(s -> System.out.println("- | " + s));
        });

        Map<String, List<String>> footers = holder.extractMetadata(CommentableMeta.FOOTER);
        System.out.println("Footer comments: ");
        footers.forEach((k, v) -> {
            if (v.isEmpty()) return;
            System.out.println("- " + k + ": ");
            v.forEach(s -> System.out.println("- | " + s));
        });


        ConfigurationTest.save(holder);
    }

    @Test
    public void testKotlin() {
        ConfigurationHolder<YAMLSource> holder = YAMLConfigFactory.from("target/kotlin.yml").build();

        ConfigurationTest.testKotlin(holder);

        ConfigurationTest.save(holder);
    }
}

