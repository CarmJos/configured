package cc.carm.lib.configuration.adapter;

import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Value adapter, used to convert the value of the configuration file into the objects.
 *
 * @param <TYPE> The type of the target value
 */
public class ValueAdapter<TYPE>
        implements ValueSerializer<TYPE>, ValueParser<TYPE> {

    protected final @NotNull ValueType<TYPE> type;

    protected @Nullable ValueSerializer<TYPE> serializer;
    protected @Nullable ValueParser<TYPE> deserializer;

    public ValueAdapter(@NotNull ValueType<TYPE> type) {
        this(type, null, null);
    }

    public ValueAdapter(@NotNull ValueType<TYPE> type,
                        @Nullable ValueSerializer<TYPE> serializer,
                        @Nullable ValueParser<TYPE> parser) {
        this.type = type;
        this.serializer = serializer;
        this.deserializer = parser;
    }

    public @NotNull ValueType<TYPE> type() {
        return type;
    }

    public @Nullable ValueSerializer<TYPE> serializer() {
        return serializer;
    }

    public @Nullable ValueParser<TYPE> parser() {
        return deserializer;
    }

    public ValueAdapter<TYPE> serializer(@Nullable ValueSerializer<TYPE> serializer) {
        this.serializer = serializer;
        return this;
    }

    public ValueAdapter<TYPE> parser(@Nullable ValueParser<TYPE> deserializer) {
        this.deserializer = deserializer;
        return this;
    }

    @Override
    public @Nullable Object serialize(
            @NotNull ConfigurationHolder<?> holder,
            @NotNull ValueType<? super TYPE> type,
            @NotNull TYPE value
    ) throws Exception {
        if (serializer == null) throw new UnsupportedOperationException("Serializer is not supported");
        return serializer.serialize(holder, type, value);
    }

    @Override
    public @Nullable TYPE parse(
            @NotNull ConfigurationHolder<?> holder,
            @NotNull ValueType<? super TYPE> type,
            @NotNull Object value
    ) throws Exception {
        if (deserializer == null) throw new UnsupportedOperationException("Deserializer is not supported");
        return deserializer.parse(holder, type, value);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ValueAdapter)) return false;
        ValueAdapter<?> that = (ValueAdapter<?>) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(type);
    }
}

