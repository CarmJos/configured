package cc.carm.lib.configuration.tests.test.sql;

import cc.carm.lib.configuration.demo.DatabaseConfiguration;
import cc.carm.lib.configuration.demo.tests.ConfigurationTest;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.json.JSONConfigFactory;
import cc.carm.lib.configuration.source.sql.SQLConfigFactory;
import cc.carm.lib.easysql.EasySQL;
import cc.carm.lib.easysql.api.SQLManager;
import cc.carm.lib.easysql.beecp.BeeDataSourceConfig;
import org.junit.Test;

import java.io.File;

public class SQLConfigTest {

    boolean local = false;

    @Test
    public void test() {
        if (!local) return;

        ConfigurationHolder<?> gsonHolder = JSONConfigFactory.from(new File("target/sql.json")).build();
        gsonHolder.initialize(DatabaseConfiguration.class);

        BeeDataSourceConfig config = new BeeDataSourceConfig();
        config.setDriverClassName(DatabaseConfiguration.DRIVER_NAME.resolve());
        config.setJdbcUrl(DatabaseConfiguration.buildJDBC());
        config.setUsername(DatabaseConfiguration.USERNAME.resolve());
        config.setPassword(DatabaseConfiguration.PASSWORD.resolve());

        SQLManager manager = EasySQL.createManager(config);
        manager.setDebugMode(true);

        ConfigurationHolder<?> holder = SQLConfigFactory.from(manager)
            .tableName("test_configs")
            .namespace("testing")
            .build();

        ConfigurationTest.testDemo(holder);
        ConfigurationTest.testInner(holder);

        ConfigurationTest.save(holder);

        EasySQL.shutdownManager(manager);
    }

}
