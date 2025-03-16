package cc.carm.lib.configuration.builder.map;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.impl.AbstractSectionBuilder;
import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.standard.ConfiguredMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SectionMapBuilder<MAP extends Map<K, V>, K, V>
        extends AbstractSectionBuilder<Map<K, V>, V, ConfiguredMap<K, V>, SectionMapBuilder<MAP, K, V>> {

    protected final @NotNull ValueType<K> keyType;

    protected @NotNull Supplier<? extends MAP> constructor;
    protected @NotNull ValueHandler<String, K> keyParser;
    protected @NotNull ValueHandler<K, String> keySerializer;

    public SectionMapBuilder(@NotNull Supplier<? extends MAP> constructor,
                             @NotNull ValueType<K> keyType, @NotNull ValueType<V> valueType,
                             @NotNull ValueHandler<String, K> keyParser,
                             @NotNull ValueHandler<K, String> keySerializer,
                             @NotNull ValueHandler<ConfigureSection, V> valueParser,
                             @NotNull ValueHandler<V, Map<String, Object>> valueSerializer) {
        super(new ValueType<Map<K, V>>() {
        }, valueType, valueParser, valueSerializer);
        this.keyType = keyType;
        this.constructor = constructor;
        this.keyParser = keyParser;
        this.keySerializer = keySerializer;
    }

    @Override
    protected @NotNull SectionMapBuilder<MAP, K, V> self() {
        return this;
    }


    public @NotNull SectionMapBuilder<MAP, K, V> parseKey(@NotNull DataFunction<String, K> keyParser) {
        return parseKey((holder, data) -> keyParser.handle(data));
    }

    public @NotNull SectionMapBuilder<MAP, K, V> parseKey(@NotNull ValueHandler<String, K> keyParser) {
        this.keyParser = keyParser;
        return this;
    }

    public @NotNull SectionMapBuilder<MAP, K, V> serializeKey(@NotNull DataFunction<K, String> keySerializer) {
        return serializeKey((holder, data) -> keySerializer.handle(data));
    }

    public @NotNull SectionMapBuilder<MAP, K, V> serializeKey(@NotNull ValueHandler<K, String> keySerializer) {
        this.keySerializer = keySerializer;
        return this;
    }

    public @NotNull SectionMapBuilder<MAP, K, V> defaults(@NotNull MAP defaults) {
        return defaults(() -> defaults);
    }

    public @NotNull SectionMapBuilder<MAP, K, V> defaults(@NotNull Consumer<MAP> defaults) {
        return defaults(() -> {
            MAP map = this.constructor.get();
            defaults.accept(map);
            return map;
        });
    }

    public @NotNull SectionMapBuilder<MAP, K, V> defaults(@NotNull K key, @NotNull V value) {
        return defaults(map -> map.put(key, value));
    }

    public @NotNull ValueAdapter<K> buildKeyAdapter() {
        return new ValueAdapter<>(this.keyType)
                .parser((holder, type, data) -> {
                    String source = holder.deserialize(String.class, data);
                    return this.keyParser.handle(holder, source);
                })
                .serializer((holder, type, data) -> {
                    String source = this.keySerializer.handle(holder, data);
                    return holder.serialize(source);
                });
    }

    @Override
    public @NotNull ConfiguredMap<K, V> build() {
        return new ConfiguredMap<>(buildManifest(), this.constructor, buildKeyAdapter(), this.buildAdapter());
    }

}
