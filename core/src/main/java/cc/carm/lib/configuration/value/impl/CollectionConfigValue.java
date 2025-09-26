package cc.carm.lib.configuration.value.impl;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueSerializer;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.value.ValueManifest;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base implementation of a collection config value, like {@link List} or {@link Set}.
 *
 * @param <V>    Value type
 * @param <C>    Collection type
 * @param <SELF> Self reference type (used for internal call or recursive generics)
 */
public abstract class CollectionConfigValue<
    V, C extends Collection<V>,
    SELF extends CollectionConfigValue<V, C, SELF>
    > extends CachedConfigValue<C, V> implements Collection<V> {

    protected final @NotNull Supplier<? extends C> constructor;
    protected final @NotNull ValueAdapter<V> paramAdapter;

    public CollectionConfigValue(@NotNull ValueManifest<C, V> manifest,
                                 @NotNull Supplier<? extends C> constructor,
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

    private @NotNull C createCollection() {
        return constructor.get();
    }

    @Override
    public @NotNull C get() {
        if (!cacheExpired()) return getCachedOrDefault(createCollection());
        // Data that is outdated and needs to be parsed again.
        C set = createCollection();
        try {
            List<?> data = config().contains(path()) ? config().getList(path()) : null;
            if (data == null) return getDefaultFirst(set);

            ValueParser<V> parser = parser();
            if (parser == null) return getDefaultFirst(set);

            int i = 0;
            for (Object dataVal : data) {
                if (dataVal == null) continue;
                try {
                    set.add(withValidated(parser.parse(holder(), paramType(), dataVal)));
                } catch (Exception e) {
                    throwing(path + "[" + i + "]", e);
                }
            }
        } catch (Exception ex) {
            throwing(ex);
        }
        return updateCache(set);
    }

    @Override
    public void set(@Nullable C collection) {
        updateCache(collection);
        if (collection == null) {
            setData(null);
            return;
        }

        ValueSerializer<V> serializer = serializer();
        if (serializer == null) return;

        List<Object> data = new ArrayList<>();
        for (V val : collection) {
            if (val == null) continue;
            try {
                data.add(serializer.serialize(holder(), paramType(), withValidated(val)));
            } catch (Exception ex) {
                throwing(ex);
            }
        }
        setData(data);
    }

    public @NotNull C copy() {
        C other = createCollection();
        other.addAll(resolve());
        return other;
    }

    public abstract @NotNull SELF self();

    public <T> @NotNull T handle(Function<C, T> function) {
        C list = resolve();
        T result = function.apply(list);
        set(list);
        return result;
    }

    public @NotNull SELF modify(Consumer<C> consumer) {
        C list = resolve();
        consumer.accept(list);
        set(list);
        return self();
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
    public boolean addAll(@NotNull Collection<? extends V> c) {
        return handle(list -> list.addAll(c));
    }

    @Override
    public boolean remove(Object o) {
        return handle(list -> list.remove(o));
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
        modify(Collection::clear);
    }

}
