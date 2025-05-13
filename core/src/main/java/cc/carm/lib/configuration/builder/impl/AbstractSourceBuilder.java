package cc.carm.lib.configuration.builder.impl;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.CommonConfigBuilder;
import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.value.ConfigValue;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractSourceBuilder<
    V, SOURCE, UNIT, RESULT extends ConfigValue<V, UNIT>,
    SELF extends AbstractSourceBuilder<V, SOURCE, UNIT, RESULT, SELF>
    > extends CommonConfigBuilder<V, UNIT, RESULT, SELF> {

    protected final @NotNull ValueType<SOURCE> sourceType;
    protected final @NotNull ValueType<UNIT> paramType;
    protected @NotNull ValueHandler<SOURCE, UNIT> valueParser;
    protected @NotNull ValueHandler<UNIT, SOURCE> valueSerializer;

    protected AbstractSourceBuilder(@NotNull ValueType<V> type,
                                    @NotNull ValueType<SOURCE> sourceType, @NotNull ValueType<UNIT> paramType,
                                    @NotNull ValueHandler<SOURCE, UNIT> parser,
                                    @NotNull ValueHandler<UNIT, SOURCE> serializer) {
        super(type);
        this.sourceType = sourceType;
        this.paramType = paramType;
        this.valueParser = parser;
        this.valueSerializer = serializer;
    }

    public @NotNull SELF parse(@NotNull DataFunction<SOURCE, UNIT> parser) {
        return parse((p, source) -> parser.handle(source));
    }

    public @NotNull SELF parse(@NotNull ValueHandler<SOURCE, UNIT> parser) {
        this.valueParser = parser;
        return self();
    }

    public @NotNull SELF serialize(@NotNull ValueHandler<UNIT, SOURCE> serializer) {
        this.valueSerializer = serializer;
        return self();
    }

    public @NotNull SELF serialize(@NotNull DataFunction<UNIT, SOURCE> serializer) {
        return serialize((p, value) -> serializer.handle(value));
    }

    protected ValueAdapter<UNIT> buildAdapter() {
        return new ValueAdapter<>(this.paramType)
            .parser((holder, type, data) -> {
                SOURCE source = holder.deserialize(this.sourceType, data);
                return this.valueParser.handle(holder, source);
            })
            .serializer((holder, type, data) -> {
                SOURCE source = this.valueSerializer.handle(holder, data);
                return holder.serialize(source);
            });
    }


}
