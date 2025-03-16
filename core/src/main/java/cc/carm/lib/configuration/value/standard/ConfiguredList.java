package cc.carm.lib.configuration.value.standard;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueSerializer;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.list.ConfigListBuilder;
import cc.carm.lib.configuration.builder.list.SourceListBuilder;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.impl.CachedConfigValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConfiguredList<V> extends CachedConfigValue<List<V>, V> implements List<V> {

    public static <T> @NotNull ConfigListBuilder<T> builderOf(@NotNull Class<T> type) {
        return builderOf(ValueType.of(type));
    }

    public static <T> @NotNull ConfigListBuilder<T> builderOf(@NotNull ValueType<T> type) {
        return new ConfigListBuilder<>(type);
    }

    public static <T> @NotNull SourceListBuilder<T, T> with(@NotNull Class<T> registeredType) {
        return with(ValueType.of(registeredType));
    }

    public static <T> @NotNull SourceListBuilder<T, T> with(@NotNull ValueType<T> registeredType) {
        return new ConfigListBuilder<>(registeredType).from(registeredType);
    }

    @SafeVarargs
    public static <T> @NotNull ConfiguredList<T> of(@NotNull T value, @NotNull T... values) {
        List<T> list = new ArrayList<>();
        list.add(value);
        Collections.addAll(list, values);
        return with(ValueType.of(value)).defaults(list).build();
    }

    protected final @NotNull Supplier<? extends List<V>> constructor;
    protected final @NotNull ValueAdapter<V> paramAdapter;

    public ConfiguredList(@NotNull ValueManifest<List<V>, V> manifest,
                          @NotNull Supplier<? extends List<V>> constructor,
                          @NotNull ValueAdapter<V> paramAdapter) {
        super(manifest);
        this.constructor = constructor;
        this.paramAdapter = paramAdapter;
    }

    /**
     * @return Adapter of this value.
     */
    public @NotNull ValueAdapter<V> adapter() {
        return this.paramAdapter;
    }

    public @NotNull ValueType<V> paramType() {
        return adapter().type();
    }

    /**
     * @return Value's parser, parse base object to value.
     */
    public @Nullable ValueParser<V> parser() {
        return parserFor(adapter());
    }

    /**
     * @return Value's serializer, parse value to base object.
     */
    public @Nullable ValueSerializer<V> serializer() {
        return serializerFor(adapter());
    }

    private @NotNull List<V> createList() {
        return constructor.get();
    }

    @Override
    public @NotNull List<V> get() {
        if (!cacheExpired()) return getCachedOrDefault(createList());
        // Data that is outdated and needs to be parsed again.
        List<V> list = createList();
        List<?> data = config().contains(path()) ? config().getList(path()) : null;
        if (data == null) return getDefaultFirst(list);

        ValueParser<V> parser = parser();
        if (parser == null) return getDefaultFirst(list);

        for (Object dataVal : data) {
            if (dataVal == null) continue;
            try {
                list.add(withValidated(parser.parse(holder(), paramType(), dataVal)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return updateCache(list);
    }

    @Override
    public void set(@Nullable List<V> list) {
        updateCache(list);
        if (list == null) {
            setData(null);
            return;
        }
        
        ValueSerializer<V> serializer = serializer();
        if (serializer == null) return;

        List<Object> data = new ArrayList<>();
        for (V val : list) {
            if (val == null) continue;
            try {
                data.add(serializer.serialize(holder(), paramType(), withValidated(val)));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        setData(data);
    }

    @Override
    public V get(int index) {
        return resolve().get(index);
    }

    public @NotNull List<V> copy() {
        return new ArrayList<>(resolve());
    }

    public <T> @NotNull T handle(Function<List<V>, T> function) {
        List<V> list = resolve();
        T result = function.apply(list);
        set(list);
        return result;
    }

    public @NotNull ConfiguredList<V> modify(Consumer<List<V>> consumer) {
        List<V> list = resolve();
        consumer.accept(list);
        set(list);
        return this;
    }

    @Override
    public V set(int index, V element) {
        return handle(list -> list.set(index, element));
    }

    @Override
    public int size() {
        return resolve().size();
    }

    @Override
    public boolean isEmpty() {
        return resolve().isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return resolve().contains(o);
    }

    @NotNull
    @Override
    public Iterator<V> iterator() {
        return resolve().iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return resolve().toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T[] a) {
        return resolve().toArray(a);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(resolve()).containsAll(c);
    }

    @Override
    public boolean add(V v) {
        handle(list -> list.add(v));
        return true;
    }

    @Override
    public void add(int index, V element) {
        modify(list -> list.add(index, element));
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        return handle(list -> list.addAll(c));
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        return handle(list -> list.addAll(index, c));
    }

    @Override
    public boolean remove(Object o) {
        return handle(list -> list.remove(o));
    }

    @Override
    public V remove(int index) {
        return handle(list -> list.remove(index));
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return handle(list -> list.removeAll(c));
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return handle(list -> list.retainAll(c));
    }

    @Override
    public void clear() {
        modify(List::clear);
    }

    @Override
    public int indexOf(Object o) {
        return resolve().indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return resolve().lastIndexOf(o);
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator() {
        return resolve().listIterator();
    }

    @NotNull
    @Override
    public ListIterator<V> listIterator(int index) {
        return resolve().listIterator(index);
    }

    @NotNull
    @Override
    public List<V> subList(int fromIndex, int toIndex) {
        return resolve().subList(fromIndex, toIndex);
    }

}