package cc.carm.lib.configuration.tests.test.mongodb;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

interface MongoConfig extends Configuration {
    ConfiguredValue<String> HOST = ConfiguredValue.of("127.0.0.1");
    ConfiguredValue<Integer> PORT = ConfiguredValue.of(27017);
    ConfiguredValue<String> USERNAME = ConfiguredValue.of("minecraft");
    ConfiguredValue<String> PASSWORD = ConfiguredValue.of("minecraft");
    ConfiguredValue<String> DATABASE = ConfiguredValue.of("minecraft");
}
