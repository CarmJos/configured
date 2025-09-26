package cc.carm.lib.configuration.value.collections;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.set.ConfigSetBuilder;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.impl.CollectionConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ConfiguredSet<V> extends CollectionConfigValue<V, Set<V>, ConfiguredSet<V>> implements Set<V> {

    public static <T> @NotNull ConfigSetBuilder<T> builderOf(@NotNull Class<T> type) {
        return builderOf(ValueType.of(type));
    }

    public static <T> @NotNull ConfigSetBuilder<T> builderOf(@NotNull ValueType<T> type) {
        return new ConfigSetBuilder<>(type);
    }

    public static <T> @NotNull ConfigSetBuilder.SourceBuilder<Object, T> with(@NotNull Class<T> registeredType) {
        return with(ValueType.of(registeredType));
    }

    public static <T> @NotNull ConfigSetBuilder.SourceBuilder<Object, T> with(@NotNull ValueType<T> registeredType) {
        return builderOf(registeredType).fromObject();
    }

    @SafeVarargs
    public static <T> @NotNull ConfiguredSet<T> of(@NotNull T value, @NotNull T... values) {
        Set<T> list = new LinkedHashSet<>();
        list.add(value);
        Collections.addAll(list, values);
        return with(ValueType.of(value)).defaults(list).build();
    }

    public ConfiguredSet(@NotNull ValueManifest<Set<V>, V> manifest,
                         @NotNull Supplier<? extends Set<V>> constructor,
                         @NotNull ValueAdapter<V> paramAdapter) {
        super(manifest, constructor, paramAdapter);
    }

    @Override
    public @NotNull ConfiguredSet<V> self() {
        return this;
    }

}
