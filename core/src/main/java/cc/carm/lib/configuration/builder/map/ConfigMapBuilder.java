package cc.carm.lib.configuration.builder.map;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Supplier;

public class ConfigMapBuilder<M extends Map<K, V>, K, V> {


    protected final @NotNull Supplier<@NotNull M> constructor;
    protected final @NotNull ValueType<K> keyType;
    protected final @NotNull ValueType<V> valueType;

    public ConfigMapBuilder(@NotNull Supplier<@NotNull M> constructor,
                            @NotNull ValueType<K> keyType, @NotNull ValueType<V> valueType) {
        this.constructor = constructor;
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public <W extends Map<K, V>> ConfigMapBuilder<W, K, V> constructor(@NotNull Supplier<W> supplier) {
        return new ConfigMapBuilder<>(supplier, keyType, valueType);
    }

    public @NotNull <S> SourceMapBuilder<M, S, K, V> from(@NotNull Class<S> clazz) {
        return from(ValueType.of(clazz));
    }

    public @NotNull <S> SourceMapBuilder<M, S, K, V> from(@NotNull ValueType<S> sourceType) {
        return from(
            sourceType,
            ValueHandler.required(keyType), ValueHandler.stringValue(),
            ValueHandler.required(valueType), ValueHandler.required()
        );
    }

    public <S> @NotNull SourceMapBuilder<M, S, K, V> from(@NotNull ValueType<S> sourceType,
                                                          @NotNull ValueHandler<String, K> keyParser,
                                                          @NotNull ValueHandler<K, String> keySerializer,
                                                          @NotNull ValueHandler<S, V> valueParser,
                                                          @NotNull ValueHandler<V, S> valueSerializer) {
        return new SourceMapBuilder<>(
            this.constructor, sourceType, keyType, valueType,
            keyParser, keySerializer, valueParser, valueSerializer
        );
    }

    public @NotNull SourceMapBuilder<M, String, K, V> fromString() {
        return from(
            ValueType.STRING,
            ValueHandler.required(keyType), ValueHandler.stringValue(),
            ValueHandler.required(valueType), ValueHandler.stringValue()
        );
    }

    public @NotNull SectionMapBuilder<M, K, V> fromSection() {
        return fromSection(
            ValueHandler.required(keyType), ValueHandler.stringValue(),
            ValueHandler.required(valueType), ValueHandler.required()
        );
    }

    public @NotNull SectionMapBuilder<M, K, V> fromSection(
        @NotNull ValueHandler<String, K> keyParser,
        @NotNull ValueHandler<K, String> keySerializer,
        @NotNull ValueHandler<ConfigureSection, V> valueParser,
        @NotNull ValueHandler<V, Map<String, Object>> valueSerializer
    ) {
        return new SectionMapBuilder<>(
            this.constructor, keyType, valueType,
            keyParser, keySerializer, valueParser, valueSerializer
        );
    }


}
