package cc.carm.lib.configuration.tests.test.mongodb;

import cc.carm.lib.configuration.demo.tests.ConfigurationTest;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.json.JSONConfigFactory;
import cc.carm.lib.configuration.source.mongodb.MongoConfigFactory;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.UuidRepresentation;
import org.junit.Test;

import java.io.File;

public class MongoTest {

    boolean local = false;

    @Test
    public void test() {

        if (!local) return;

        ConfigurationHolder<?> gsonHolder = JSONConfigFactory.from(new File("target/mongo.json")).build();
        gsonHolder.initialize(MongoConfig.class);

        MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(new ConnectionString(
                "mongodb://" + MongoConfig.HOST.resolve() + ":" + MongoConfig.PORT.resolve()
            ))
            .credential(MongoCredential.createCredential(
                MongoConfig.USERNAME.resolve(), MongoConfig.DATABASE.resolve(),
                MongoConfig.PASSWORD.resolve().toCharArray()
            ))
            .uuidRepresentation(UuidRepresentation.STANDARD)
            .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase mongoDatabase = mongoClient.getDatabase(MongoConfig.DATABASE.resolve());

        ConfigurationHolder<?> mongoHolder = MongoConfigFactory
            .from(mongoDatabase, "configs")
            .namespace("my_plugin")
            .build();

        // Test the configuration
        ConfigurationTest.testDemo(mongoHolder);
        ConfigurationTest.testInner(mongoHolder);

        ConfigurationTest.save(mongoHolder);

    }


}
