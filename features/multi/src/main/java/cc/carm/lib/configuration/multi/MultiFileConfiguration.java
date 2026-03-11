package cc.carm.lib.configuration.multi;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultiFileConfiguration<K, T> extends MultiConfiguration<K, T> {

    protected final @NotNull File dataFolder;
    protected final @NotNull String extensionSuffix; // e.g. ".yml"

    protected final ConcurrentHashMap<K, ConfigurationHolder<?>> holders = new ConcurrentHashMap<>();

    public MultiFileConfiguration(@NotNull File dataFolder, @NotNull String extensionSuffix) {
        this.dataFolder = dataFolder;
        this.extensionSuffix = extensionSuffix;

        // Load existing configuration files
        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(extensionSuffix));
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName();
                    String keyStr = fileName.substring(0, fileName.length() - extensionSuffix.length()); // Remove extension suffix
                    try {
                        holders.put(extractKeyFromFilename(keyStr), loadHolder(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        loadAll();
    }

    public abstract K extractKeyFromFilename(@NotNull String fileName);

    public String keyToFilename(@NotNull K key) {
        return key.toString();
    }

    public abstract ConfigurationHolder<?> loadHolder(@NotNull File file);

    @Override
    public @NotNull Map<K, ConfigurationHolder<?>> holders() {
        return this.holders;
    }

    @Override
    public @NotNull ConfigurationHolder<?> holder(@NotNull K key) {
        ConfigurationHolder<?> loaded = holders.get(key);
        if (loaded != null) return loaded;

        File file = new File(dataFolder, keyToFilename(key) + this.extensionSuffix);
        ConfigurationHolder<?> created = loadHolder(file);
        holders.put(key, created);
        return created;
    }

    @Override
    public void removeHolder(@NotNull K key) {
        ConfigurationHolder<?> loaded = holders.remove(key);
        if (loaded == null) return;

        File file = new File(dataFolder, key + ".yml");
        if (file.exists()) file.delete();
    }

}
