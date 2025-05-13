package sample;

import cc.carm.lib.configuration.Configuration;
import cc.carm.lib.configuration.annotation.ConfigPath;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.value.standard.ConfiguredList;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;

import java.util.UUID;

@ConfigPath(root = true)
@HeaderComments("Configurations for sample")
public interface SampleConfig extends Configuration {
    ConfiguredValue<Boolean> ENABLED = ConfiguredValue.of(true);

    @HeaderComments("Server configurations") // Header comment
    ConfiguredValue<Integer> PORT = ConfiguredValue.of(Integer.class);

    @HeaderComments({"[ UUID >-----------------------------------", "A lot of UUIDs"})
    ConfiguredList<UUID> UUIDS = ConfiguredList.builderOf(UUID.class).fromString()
        .parse(UUID::fromString).serialize(UUID::toString)
        .defaults(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            UUID.fromString("00000000-0000-0000-0000-000000000001")
        ).build();

    @ConfigPath("info") // Custom path
    interface INFO extends Configuration {

        @HeaderComments("Configure your name!") // Header comment
        ConfiguredValue<String> NAME = ConfiguredValue.of("Joker");

        @ConfigPath("how-old-are-you") // Custom path
        ConfiguredValue<Integer> AGE = ConfiguredValue.of(24);

    }

}
