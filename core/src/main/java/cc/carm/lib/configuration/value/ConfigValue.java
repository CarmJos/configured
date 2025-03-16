package cc.carm.lib.configuration.value;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a configurable value with type safety and null-handling capabilities.
 * <p>
 * This abstract class provides core functionalities for managing configuration values,
 * including value retrieval with fallback defaults, null safety enforcement, and value
 * persistence controls. It serves as the foundation for type-specific configuration
 * implementations.
 * </p>
 *
 * <h3>Functions:</h3>
 * <ul>
 *   <li>Type-safe value access through {@link #get()} and {@link #optional()}</li>
 *   <li>Default value fallback via {@link #getOrDefault()}</li>
 *   <li>Null-safety enforcement with {@link #resolve()} and {@link #getNotNull()}</li>
 *   <li>Default value initialization through {@link #setDefault()}</li>
 *   <li>Value comparison with {@link #isDefault()}</li>
 * </ul>
 *
 * <h3>Persistence Behavior:</h3>
 * Value modifications via {@link #set(Object)} or {@link #setDefault()} methods
 * <b>do NOT automatically persist</b> to configuration sources. Explicit calls to
 * {@link ConfigurationHolder#save()} are required for permanent storage.
 *
 * @see ValueManifest Base class providing metadata and default value handling
 * @see ConfigurationHolder Responsible for configuration source persistence
 */
public abstract class ConfigValue<T, U> extends ValueManifest<T, U> {

    protected ConfigValue(@NotNull ValueManifest<T, U> manifest) {
        super(manifest);
    }

    /**
     * Gets the configured value (i.e., the value read from the source).
     * <br> If no default value was written during initialization, you can use
     * the {@link #getOrDefault()} method to obtain the default value when this value is empty.
     *
     * @return Configured value
     */
    public abstract @Nullable T get();

    /**
     * Gets the configured value, or returns the default value if not present.
     *
     * @return Configured value or default value
     */
    public T getOrDefault() {
        return optional().orElse(defaults());
    }

    /**
     * Gets the non-null value of this configuration.
     *
     * @return Non-null value
     * @throws NullPointerException Thrown when the corresponding data is null
     */
    public @NotNull T resolve() {
        return Objects.requireNonNull(getOrDefault(), "Value(" + type() + ") @[" + path() + "] is null.");
    }

    /**
     * Gets the non-null value of this configuration.
     *
     * @return Non-null value
     * @throws NullPointerException Thrown when the corresponding data is null
     * @see #resolve() for a more descriptive function
     */
    public @NotNull T getNotNull() {
        return resolve();
    }

    /**
     * Gets the value of this configuration as an {@link Optional}.
     *
     * @return {@link Optional} value
     */
    public @NotNull Optional<@Nullable T> optional() {
        return Optional.ofNullable(get());
    }

    /**
     * Sets the value of this configuration.
     * <br> After setting, the configuration file will NOT be saved automatically.
     * To save, call {@link ConfigurationHolder#save()}.
     *
     * @param value The value to set
     */
    public abstract void set(@Nullable T value);

    /**
     * Initializes the default value for this configuration.
     * <br> After setting, the configuration file will NOT be saved automatically.
     * To save, call {@link ConfigurationHolder#save()}.
     */
    public void setDefault() {
        setDefault(false);
    }

    /**
     * Sets the configuration value to its default.
     * <br> After setting, the configuration file will NOT be saved automatically.
     * To save, call {@link ConfigurationHolder#save()}.
     *
     * @param override Whether to overwrite existing configured value
     */
    public void setDefault(boolean override) {
        if (config().contains(path()) && !override) return; // Skip if the value is already set
        set(defaults()); // Set the default value
    }

    /**
     * Checks if the loaded configuration value matches the default value.
     *
     * @return Whether the current value is the default value
     */
    public boolean isDefault() {
        return Objects.equals(defaults(), get());
    }

    /**
     * Try to save the configuration.
     * <br>To save multiple modifications,
     * it is recommended to call {@link ConfigurationHolder#save()}
     * after all modifications are completed instead of this.
     *
     * @throws Exception Thrown when an error occurs during saving
     */
    public void save() throws Exception {
        holder().save();
    }

}