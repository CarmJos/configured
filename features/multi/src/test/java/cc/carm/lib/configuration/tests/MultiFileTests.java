package cc.carm.lib.configuration.tests;

import cc.carm.lib.configuration.multi.MultiFileConfiguration;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.source.yaml.YAMLConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

public class MultiFileTests {


    @Test
    public void test() {

        ProfileStorage storage = new ProfileStorage(new File(new File("target"), "test-profiles"));
        UserProfile profile = new UserProfile(UUID.randomUUID(), "John Doe", "john@google.com", "123123123");
        storage.update(profile.getUniqueId(), profile);

        System.out.println("Current users: ");
        storage.values().forEach((k, v) -> {
            System.out.println("# " + k);
            System.out.println("- " + v.getName() + " (" + v.getEmail() + ", " + v.getPhone() + ")");
        });

    }

    public static class ProfileStorage extends MultiFileConfiguration<UUID, UserProfile> {


        public ProfileStorage(@NotNull File dataFolder) {
            super(dataFolder, ".yml");
        }

        @Override
        public UserProfile read(@NotNull UUID key, @NotNull ConfigurationHolder<?> holder) {
            ConfigureSection conf = holder.config();
            return new UserProfile(
                key,
                conf.getString("name", "Unknown"),
                conf.getString("email", "unset"),
                conf.getString("phone", "123123123")
            );
        }

        @Override
        public void write(@NotNull ConfigurationHolder<?> holder, @NotNull UserProfile value) {
            ConfigureSection conf = holder.config();
            conf.set("name", value.getName());
            conf.set("email", value.getEmail());
            conf.set("phone", value.getPhone());
            try {
                holder.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public UUID extractKeyFromFilename(@NotNull String fileName) {
            return UUID.fromString(fileName);
        }

        @Override
        public ConfigurationHolder<?> loadHolder(@NotNull File file) {
            return YAMLConfigFactory.from(file).build();
        }
    }


}
