package cc.carm.lib.configuration.builder.impl;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.CommonConfigBuilder;
import cc.carm.lib.configuration.function.DataFunction;
import cc.carm.lib.configuration.function.ValueComposer;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.ConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractSectionBuilder<
        TYPE, UNIT,
        RESULT extends ConfigValue<TYPE, UNIT>,
        SELF extends AbstractSectionBuilder<TYPE, UNIT, RESULT, SELF>
        > extends CommonConfigBuilder<TYPE, UNIT, RESULT, SELF> {


    protected final @NotNull ValueType<UNIT> paramType;

    protected @NotNull ValueHandler<ConfigureSection, UNIT> parser;
    protected @NotNull ValueHandler<UNIT, ? extends Map<String, Object>> serializer;

    protected AbstractSectionBuilder(@NotNull ValueType<TYPE> type, @NotNull ValueType<UNIT> paramType,
                                     @NotNull ValueHandler<ConfigureSection, UNIT> parser,
                                     @NotNull ValueHandler<UNIT, ? extends Map<String, Object>> serializer) {
        super(type);
        this.paramType = paramType;
        this.parser = parser;
        this.serializer = serializer;
    }

    public @NotNull SELF parse(@NotNull DataFunction<ConfigureSection, UNIT> valueParser) {
        return parse((p, section) -> valueParser.handle(section));
    }

    public @NotNull SELF parse(@NotNull ValueHandler<ConfigureSection, UNIT> valueParser) {
        this.parser = valueParser;
        return self();
    }

    public @NotNull SELF serialize(@NotNull ValueHandler<UNIT, ? extends Map<String, Object>> serializer) {
        this.serializer = serializer;
        return self();
    }

    public @NotNull SELF serialize(@NotNull DataFunction<UNIT, ? extends Map<String, Object>> serializer) {
        return serialize((p, value) -> serializer.handle(value));
    }

    public @NotNull SELF serialize(@NotNull ValueComposer<Map<String, Object>, UNIT> serializer) {
        return serialize((h, value) -> {
            Map<String, Object> map = new LinkedHashMap<>();
            serializer.accept(h, map, value);
            return map;
        });
    }

    protected ValueAdapter<UNIT> buildAdapter() {
        return new ValueAdapter<>(this.paramType)
                .parser((p, type, data) -> {
                    ConfigureSection section = p.deserialize(ConfigureSection.class, data);
                    if (section == null) return null;
                    return this.parser.handle(p, section);
                })
                .serializer((p, type, data) -> {
                    Map<String, Object> map = this.serializer.handle(p, data);
                    return map == null || map.isEmpty() ? null : map;
                });
    }

}
