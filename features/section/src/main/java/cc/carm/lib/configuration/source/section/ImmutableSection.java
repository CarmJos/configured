package cc.carm.lib.configuration.source.section;

import cc.carm.lib.configuration.function.DataFunction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ImmutableSection implements ConfigureSection {

    public static ImmutableSection of(@NotNull ConfigureSection section) {
        if (section instanceof ImmutableSection) {
            return (ImmutableSection) section;
        } else return new ImmutableSection(null, section);
    }

    protected final @Nullable ImmutableSection parent;
    protected final @NotNull ConfigureSection raw;

    private ImmutableSection(@Nullable ImmutableSection parent, @NotNull ConfigureSection raw) {
        this.parent = parent;
        this.raw = raw;
    }

    private @NotNull ConfigureSection raw() {
        return raw;
    }


    @Override
    public @Nullable ImmutableSection parent() {
        return this.parent;
    }

    @Override
    public @NotNull String path() {
        return raw().path();
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Object> getValues(boolean deep) {
        return raw().getValues(deep);
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        throw new IllegalStateException("This section is not modifiable!");
    }

    @Override
    public void remove(@NotNull String path) {
        throw new IllegalStateException("This section is not modifiable!");
    }

    @Override
    public @NotNull ImmutableSection createSection(@NotNull String path, @NotNull Map<?, ?> data) {
        return new ImmutableSection(this, raw().createSection(path, data));
    }

    @Override
    public @Nullable Object get(@NotNull String path) {
        Object value = raw().get(path);
        if (value instanceof ConfigureSection && !(value instanceof ImmutableSection)) {
            return new ImmutableSection(this, (ConfigureSection) value);
        }
        return value;
    }

    @Override
    public @Nullable ConfigureSection getSection(@NotNull String path) {
        ConfigureSection get = raw().getSection(path);
        if (!(get instanceof ImmutableSection)) {
            return new ImmutableSection(this, get);
        }
        return get;
    }

    @Override
    public char pathSeparator() {
        return raw().pathSeparator();
    }

    @Override
    public boolean isRoot() {
        return raw().isRoot();
    }

    @Override
    public boolean isEmpty() {
        return raw().isEmpty();
    }

    @Override
    public @NotNull @UnmodifiableView Set<String> getKeys(boolean deep) {
        return raw().getKeys(deep);
    }

    @Override
    public @NotNull @UnmodifiableView Set<String> keys() {
        return raw().keys();
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Object> values() {
        return raw().values();
    }

    @Override
    public @NotNull @UnmodifiableView Map<String, Object> asMap() {
        return raw().asMap();
    }

    @Override
    public Stream<Map.Entry<String, Object>> stream() {
        return raw().stream();
    }

    @Override
    public void forEach(@NotNull BiConsumer<String, Object> action) {
        raw().forEach(action);
    }

    @Override
    public boolean contains(@NotNull String path) {
        return raw().contains(path);
    }

    @Override
    public boolean containsValue(@NotNull String path) {
        return raw().containsValue(path);
    }

    @Override
    public <T> boolean isType(@NotNull String path, @NotNull Class<T> typeClass) {
        return raw().isType(path, typeClass);
    }

    @Override
    public boolean isList(@NotNull String path) {
        return raw().isList(path);
    }

    @Override
    public @Nullable List<?> getList(@NotNull String path) {
        return raw().getList(path);
    }

    @Override
    public boolean isSection(@NotNull String path) {
        return raw().isSection(path);
    }

    @Override
    public <T> @Nullable T get(@NotNull String path, @NotNull Class<T> type) {
        return raw().get(path, type);
    }

    @Override
    public <T> @Nullable T get(@NotNull String path, @NotNull DataFunction<@Nullable Object, T> parser) {
        return raw().get(path, parser);
    }

    @Override
    public <T> @Nullable T get(@NotNull String path, T defaults, @NotNull Class<T> clazz) {
        return raw().get(path, defaults, clazz);
    }

    @Override
    public <T> @Nullable T get(@NotNull String path, T defaultValue, @NotNull DataFunction<Object, T> parser) {
        return raw().get(path, defaultValue, parser);
    }

    @Override
    public boolean isBoolean(@NotNull String path) {
        return raw().isBoolean(path);
    }

    @Override
    public boolean getBoolean(@NotNull String path) {
        return raw().getBoolean(path);
    }

    @Override
    public @Nullable Boolean getBoolean(@NotNull String path, @Nullable Boolean def) {
        return raw().getBoolean(path, def);
    }

    @Override
    public @Nullable Boolean isByte(@NotNull String path) {
        return raw().isByte(path);
    }

    @Override
    public @Nullable Byte getByte(@NotNull String path) {
        return raw().getByte(path);
    }

    @Override
    public @Nullable Byte getByte(@NotNull String path, @Nullable Byte def) {
        return raw().getByte(path, def);
    }

    @Override
    public boolean isShort(@NotNull String path) {
        return raw().isShort(path);
    }

    @Override
    public @Nullable Short getShort(@NotNull String path) {
        return raw().getShort(path);
    }

    @Override
    public @Nullable Short getShort(@NotNull String path, @Nullable Short def) {
        return raw().getShort(path, def);
    }

    @Override
    public boolean isInt(@NotNull String path) {
        return raw().isInt(path);
    }

    @Override
    public @Nullable Integer getInt(@NotNull String path) {
        return raw().getInt(path);
    }

    @Override
    public @Nullable Integer getInt(@NotNull String path, @Nullable Integer def) {
        return raw().getInt(path, def);
    }

    @Override
    public boolean isLong(@NotNull String path) {
        return raw().isLong(path);
    }

    @Override
    public @Nullable Long getLong(@NotNull String path) {
        return raw().getLong(path);
    }

    @Override
    public @Nullable Long getLong(@NotNull String path, @Nullable Long def) {
        return raw().getLong(path, def);
    }

    @Override
    public boolean isFloat(@NotNull String path) {
        return raw().isFloat(path);
    }

    @Override
    public @Nullable Float getFloat(@NotNull String path) {
        return raw().getFloat(path);
    }

    @Override
    public @Nullable Float getFloat(@NotNull String path, @Nullable Float def) {
        return raw().getFloat(path, def);
    }

    @Override
    public boolean isDouble(@NotNull String path) {
        return raw().isDouble(path);
    }

    @Override
    public @Nullable Double getDouble(@NotNull String path) {
        return raw().getDouble(path);
    }

    @Override
    public @Nullable Double getDouble(@NotNull String path, @Nullable Double def) {
        return raw().getDouble(path, def);
    }

    @Override
    public boolean isChar(@NotNull String path) {
        return raw().isChar(path);
    }

    @Override
    public @Nullable Character getChar(@NotNull String path) {
        return raw().getChar(path);
    }

    @Override
    public @Nullable Character getChar(@NotNull String path, @Nullable Character def) {
        return raw().getChar(path, def);
    }

    @Override
    public boolean isString(@NotNull String path) {
        return raw().isString(path);
    }

    @Override
    public @Nullable String getString(@NotNull String path) {
        return raw().getString(path);
    }

    @Override
    public @Nullable String getString(@NotNull String path, @Nullable String def) {
        return raw().getString(path, def);
    }

    @Override
    public @NotNull <V> List<V> getList(@NotNull String path, @NotNull DataFunction<Object, V> parser) {
        return raw().getList(path, parser);
    }

    @Override
    public @NotNull List<String> getStringList(@NotNull String path) {
        return raw().getStringList(path);
    }

    @Override
    public @NotNull List<Integer> getIntegerList(@NotNull String path) {
        return raw().getIntegerList(path);
    }

    @Override
    public @NotNull List<Long> getLongList(@NotNull String path) {
        return raw().getLongList(path);
    }

    @Override
    public @NotNull List<Double> getDoubleList(@NotNull String path) {
        return raw().getDoubleList(path);
    }

    @Override
    public @NotNull List<Float> getFloatList(@NotNull String path) {
        return raw().getFloatList(path);
    }

    @Override
    public @NotNull List<Byte> getByteList(@NotNull String path) {
        return raw().getByteList(path);
    }

    @Override
    public @NotNull List<Character> getCharList(@NotNull String path) {
        return raw().getCharList(path);
    }

    @Override
    public @NotNull List<ConfigureSection> getSectionList(@NotNull String path) {
        return raw().getSectionList(path);
    }

    @Override
    public <T, C extends Collection<T>> @NotNull C getCollection(@NotNull String path, @NotNull Supplier<C> constructor, @NotNull DataFunction<Object, T> parser) {
        return raw().getCollection(path, constructor, parser);
    }

    @Override
    public @NotNull Stream<?> stream(@NotNull String path) {
        return raw().stream(path);
    }

    @Override
    public @NotNull <T> Stream<T> stream(@NotNull String path, @NotNull Function<Object, T> parser) {
        return raw().stream(path, parser);
    }

    @Override
    public String childPath(String path) {
        return raw().childPath(path);
    }

    @Override
    public int hashCode() {
        return raw.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equals(raw, obj);
    }

}
