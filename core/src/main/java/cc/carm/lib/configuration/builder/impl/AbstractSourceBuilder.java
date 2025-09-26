package cc.carm.lib.configuration.builder.impl;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueSerializer;
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

    @SuppressWarnings("NotNullFieldNotInitialized") // Already initialized in constructor
    protected @NotNull ValueParser<UNIT> valueParser;
    @SuppressWarnings("NotNullFieldNotInitialized") // Already initialized in constructor
    protected @NotNull ValueSerializer<UNIT> valueSerializer;

    protected AbstractSourceBuilder(@NotNull ValueType<V> type,
                                    @NotNull ValueType<SOURCE> sourceType, @NotNull ValueType<UNIT> paramType,
                                    @NotNull ValueHandler<SOURCE, UNIT> parser,
                                    @NotNull ValueHandler<UNIT, SOURCE> serializer) {
        super(type);
        this.sourceType = sourceType;
        this.paramType = paramType;
        parse(parser);
        serialize(serializer);
    }

    public @NotNull SELF parse(@NotNull DataFunction<SOURCE, UNIT> parser) {
        return parse((p, source) -> parser.handle(source));
    }

    public @NotNull SELF parse(@NotNull ValueHandler<SOURCE, UNIT> parser) {
        return parser((holder, type, data) -> {
            SOURCE source = holder.deserialize(this.sourceType, data);
            return parser.handle(holder, source);
        });
    }

    public @NotNull SELF parser(@NotNull ValueParser<UNIT> parser) {
        this.valueParser = parser;
        return self();
    }

    public @NotNull SELF serialize(@NotNull ValueHandler<UNIT, SOURCE> serializer) {
        return serializer((holder, type, data) -> {
            SOURCE source = serializer.handle(holder, data);
            return holder.serialize(source);
        });
    }

    public @NotNull SELF serialize(@NotNull DataFunction<UNIT, SOURCE> serializer) {
        return serialize((p, value) -> serializer.handle(value));
    }

    public @NotNull SELF serializer(@NotNull ValueSerializer<UNIT> serializer) {
        this.valueSerializer = serializer;
        return self();
    }

    protected ValueAdapter<UNIT> buildAdapter() {
        return new ValueAdapter<>(this.paramType, this.valueSerializer, this.valueParser);
    }


}
