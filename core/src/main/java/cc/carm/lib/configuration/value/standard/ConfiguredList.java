package cc.carm.lib.configuration.value.standard;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.collection.SimpleCollectionCreator;
import cc.carm.lib.configuration.builder.list.ConfigListCreator;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.impl.CollectionConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

public class ConfiguredList<V> extends CollectionConfigValue<V, List<V>, ConfiguredList<V>> implements List<V> {

    public static <T> @NotNull ConfigListCreator<T> builderOf(@NotNull Class<T> type) {
        return builderOf(ValueType.of(type));
    }

    public static <T> @NotNull ConfigListCreator<T> builderOf(@NotNull ValueType<T> type) {
        return new ConfigListCreator<>(type);
    }

    public static <T>
    @NotNull SimpleCollectionCreator.Source<Object, T, List<T>, ConfiguredList<T>> with(@NotNull Class<T> registeredType) {
        return with(ValueType.of(registeredType));
    }

    public static <T> @NotNull SimpleCollectionCreator.Source<Object, T, List<T>, ConfiguredList<T>> with(@NotNull ValueType<T> registeredType) {
        return builderOf(registeredType).fromObject();
    }

    @SafeVarargs
    public static <T> @NotNull ConfiguredList<T> of(@NotNull T value, @NotNull T... values) {
        List<T> list = new ArrayList<>();
        list.add(value);
        Collections.addAll(list, values);
        return with(ValueType.of(value)).defaults(list).build();
    }

    public ConfiguredList(@NotNull ValueManifest<List<V>, V> manifest,
                          @NotNull Supplier<? extends List<V>> constructor,
                          @NotNull ValueAdapter<V> paramAdapter) {
        super(manifest, constructor, paramAdapter);
    }

    @Override
    public V get(int index) {
        return resolve().get(index);
    }

    @Override
    public @NotNull ConfiguredList<V> self() {
        return this;
    }

    @Override
    public V set(int index, V element) {
        return handle(list -> list.set(index, element));
    }


    @Override
    public void add(int index, V element) {
        modify(list -> list.add(index, element));
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends V> c) {
        return handle(list -> list.addAll(index, c));
    }

    @Override
    public V remove(int index) {
        return handle(list -> list.remove(index));
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
