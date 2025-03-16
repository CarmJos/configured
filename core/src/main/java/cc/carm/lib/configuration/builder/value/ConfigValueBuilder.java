package cc.carm.lib.configuration.builder.value;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ConfigValueBuilder<V> {

    protected final @NotNull ValueType<V> type;

    public ConfigValueBuilder(@NotNull ValueType<V> type) {
        this.type = type;
    }

    public @NotNull <S> SourceValueBuilder<S, V> from(@NotNull Class<S> clazz) {
        return from(ValueType.of(clazz));
    }

    public @NotNull <S> SourceValueBuilder<S, V> from(@NotNull ValueType<S> sourceType) {
        return from(sourceType, ValueHandler.required(type), ValueHandler.required(sourceType));
    }

    public @NotNull <S> SourceValueBuilder<S, V> from(@NotNull ValueType<S> sourceType,
                                                      @NotNull ValueHandler<S, V> valueParser,
                                                      @NotNull ValueHandler<V, S> valueSerializer) {
        return new SourceValueBuilder<>(sourceType, this.type, valueParser, valueSerializer);
    }

    public @NotNull SourceValueBuilder<String, V> fromString() {
        return from(ValueType.STRING, ValueHandler.required(type), ValueHandler.stringValue());
    }

    public @NotNull SectionValueBuilder<V> fromSection() {
        return fromSection(ValueHandler.required(type), ValueHandler.required());
    }

    public @NotNull SectionValueBuilder<V> fromSection(
            @NotNull ValueHandler<ConfigureSection, V> valueParser,
            @NotNull ValueHandler<V, ? extends Map<String, Object>> valueSerializer
    ) {
        return new SectionValueBuilder<>(this.type, valueParser, valueSerializer);
    }

}
