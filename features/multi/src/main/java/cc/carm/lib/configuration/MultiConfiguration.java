package cc.carm.lib.configuration;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public abstract class MultiConfiguration<K, T> {

    protected final ConcurrentHashMap<K, T> valuesCache = new ConcurrentHashMap<>();

    public MultiConfiguration() {
    }

    /**
     * Read the value of the key from the holder, and return the value.
     *
     * @param key    The key of the value to read.
     * @param holder The holder of the value to read.
     * @return The value of the key, or null if the value is not exist.
     */
    public abstract T read(@NotNull K key, @NotNull ConfigurationHolder<?> holder);

    /**
     * Write (and save) the value of the key to the holder, then update the cache.
     *
     * @param holder The holder of the value to write.
     * @param value  The value to write, which should not be null.
     */
    public abstract void write(@NotNull ConfigurationHolder<?> holder, @NotNull T value);

    /**
     * Get all holders of the configuration, which should be a concurrent map to support concurrent access.
     *
     * @return All holders of the configuration.
     */
    public abstract @NotNull Map<K, ConfigurationHolder<?>> holders();

    /**
     * Get the holder of the key.
     * If the holder of the key is not exist,
     * it should be created then return the created holder.
     *
     * @param key The key of the holder to get.
     * @return The holder of the key.
     */
    public abstract @NotNull ConfigurationHolder<?> holder(@NotNull K key);

    /**
     * Remove the holder of the key, and remove the value of the key from the cache.
     * Also delete the configuration file if necessary.
     *
     * @param key The key of the holder to remove.
     */
    public abstract void removeHolder(@NotNull K key);

    public void loadAll() {
        for (Map.Entry<K, ConfigurationHolder<?>> entry : holders().entrySet()) {
            K key = entry.getKey();
            ConfigurationHolder<?> holder = entry.getValue();
            try {
                T loaded = read(key, holder);
                if (loaded != null) {
                    valuesCache.put(key, loaded);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveAll() {
        for (Map.Entry<K, T> entry : valuesCache.entrySet()) {
            K key = entry.getKey();
            T value = entry.getValue();
            ConfigurationHolder<?> holder = holder(key);
            try {
                write(holder, value);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * Get all keys in the cache.
     *
     * @return All keys in the cache.
     */
    public @NotNull Set<K> keys() {
        return valuesCache.keySet();
    }

    /**
     * Get all values in the cache.
     *
     * @return All values in the cache.
     */
    public @NotNull Map<K, T> values() {
        return this.valuesCache;
    }

    /**
     * Get the value of the key, or null if the value is not exist.
     *
     * @param key The key of the value to get.
     * @return The value of the key, or null if the value is not exist.
     */
    public @Nullable T get(@NotNull K key) {
        return valuesCache.get(key);
    }

    /**
     * Update the value of the key, and return the old value.
     *
     * @param key   The key of the value to update.
     * @param value The new value, or null to remove the value.
     * @return The old value, or null if the value is not exist.
     */
    public @Nullable T update(@NotNull K key, @Nullable T value) {
        if (value == null) {
            T current = valuesCache.remove(key);
            removeHolder(key);
            return current;
        }
        ConfigurationHolder<?> holder = holder(key);
        write(holder, value);
        return valuesCache.put(key, value);
    }

}
