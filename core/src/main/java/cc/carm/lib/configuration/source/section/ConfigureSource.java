package cc.carm.lib.configuration.source.section;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.option.StandardOptions;
import org.jetbrains.annotations.*;

import java.util.Map;
import java.util.Set;

/**
 * ConfigureSource represents the source of configuration,
 * which can be a file, a database, or any other source.
 *
 * @param <SECTION>  The type of the root section.
 * @param <ORIGINAL> The original configuration object.
 * @param <SELF>     The type of the source itself, for further implement support.
 * @see ConfigureSection
 */
public abstract class ConfigureSource<
    SECTION extends ConfigureSection, ORIGINAL,
    SELF extends ConfigureSource<SECTION, ORIGINAL, SELF>>
    implements ConfigureSection {

    protected final @NotNull ConfigurationHolder<? extends SELF> holder;
    protected long lastUpdateMillis;

    protected ConfigureSource(@NotNull ConfigurationHolder<? extends SELF> holder, long lastUpdateMillis) {
        this.holder = holder;
        this.lastUpdateMillis = lastUpdateMillis;
    }

    public @NotNull ConfigurationHolder<? extends SELF> holder() {
        return holder;
    }

    public void reload() throws Exception {
        onReload(); // 调用重写的Reload方法
        this.lastUpdateMillis = System.currentTimeMillis();
    }

    @Contract(pure = true)
    @ApiStatus.Internal
    protected abstract @NotNull SELF self();

    /**
     * @return The original configuration object.
     */
    @Contract(pure = true)
    public abstract @NotNull ORIGINAL original();

    /**
     * @return The root {@link ConfigureSection}, which represents the entire configuration.
     */
    public abstract @NotNull SECTION section();

    /**
     * Save the whole configuration.
     *
     * @throws Exception If any error occurs while saving.
     */
    public abstract void save() throws Exception;

    /**
     * Reload the configuration.
     * <br>This used for implementation, for external usage, use {@link #reload()}
     *
     * @throws Exception If any error occurs while reloading.
     */
    @ApiStatus.OverrideOnly
    protected abstract void onReload() throws Exception;

    public char pathSeparator() {
        return holder().options().get(StandardOptions.PATH_SEPARATOR);
    }

    public long getLastUpdateMillis() {
        return this.lastUpdateMillis;
    }

    public boolean isExpired(long parsedTime) {
        return getLastUpdateMillis() > parsedTime;
    }

    /**
     * Source also represents the root section, so it has no parent
     *
     * @return null
     */
    @Override
    @Contract(pure = true, value = "->null")
    public @Nullable ConfigureSection parent() {
        return null;
    }

    @Override
    public @NotNull String path() {
        return "";
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        return section().getValues(deep);
    }

    @Override
    public @NotNull @UnmodifiableView Set<String> getKeys(boolean deep) {
        return section().getKeys(deep);
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Object> asMap() {
        return section().asMap();
    }

    @Override
    public @NotNull ConfigureSection createSection(@NotNull String path, @NotNull Map<?, ?> data) {
        return section().createSection(path, data);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        section().set(path, value);
    }

    @Override
    public void remove(@NotNull String path) {
        section().remove(path);
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        return section().get(path);
    }

}
