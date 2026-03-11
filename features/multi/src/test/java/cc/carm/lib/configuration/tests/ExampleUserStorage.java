package cc.carm.lib.configuration.tests;

import cc.carm.lib.configuration.MultiConfiguration;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.yaml.YAMLConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ExampleUserStorage<T> extends MultiConfiguration<UUID, T> {

    protected final @NotNull File dataFolder;
    protected final ConcurrentHashMap<UUID, ConfigurationHolder<?>> holders = new ConcurrentHashMap<>();

    public ExampleUserStorage(@NotNull File dataFolder) {
        this.dataFolder = dataFolder;

        // Load existing configuration files
        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String uuidStr = fileName.substring(0, fileName.length() - 4); // Remove ".yml"
                    try {
                        UUID uuid = UUID.fromString(uuidStr);
                        ConfigurationHolder<?> holder = YAMLConfigFactory.from(file).build();
                        holders.put(uuid, holder);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Invalid UUID in file name: " + fileName);
                    }
                }
            }
        }

        loadAll();
    }

    @Override
    public @NotNull Map<UUID, ConfigurationHolder<?>> holders() {
        return this.holders;
    }

    @Override
    public @NotNull ConfigurationHolder<?> holder(@NotNull UUID key) {
        ConfigurationHolder<?> loaded = holders.get(key);
        if (loaded != null) return loaded;

        ConfigurationHolder<?> created = YAMLConfigFactory.from(new File(dataFolder, key + ".yml")).build();
        holders.put(key, created);
        return created;
    }

    @Override
    public void removeHolder(@NotNull UUID key) {
        ConfigurationHolder<?> loaded = holders.remove(key);
        if (loaded == null) return;

        File file = new File(dataFolder, key + ".yml");
        if (file.exists()) file.delete();
    }

}
