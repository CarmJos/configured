package cc.carm.lib.configuration.adapter.strandard;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueParser;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.DataFunction;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class PrimitiveAdapter<T> extends ValueAdapter<T> {

    public static final String[] TRUE_VALUES = new String[]{
        "true", "yes", "on", "1", "enabled", "enable", "active"
    };

    public static final String[] FALSE_VALUES = new String[]{
        "false", "no", "off", "0", "disabled", "disable", "inactive"
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ValueAdapter<Enum<?>> ofEnum() {
        ValueAdapter<Enum<?>> adapter = new ValueAdapter<>(new ValueType<Enum<?>>() {
        });
        adapter.parser((provider, type, data) -> Enum.valueOf((Class<Enum>) type.getRawType(), data.toString()));
        adapter.serializer((provider, type, value) -> value.name());
        return adapter;
    }

    public static PrimitiveAdapter<String> ofString() {
        return of(String.class, o -> o instanceof String ? (String) o : o.toString());
    }

    public static PrimitiveAdapter<Boolean> ofBoolean() {
        return of(Boolean.class, data -> {
            if (data instanceof Boolean) return (Boolean) data;
            String v = data.toString().trim();
            if (Arrays.stream(TRUE_VALUES).anyMatch(v::equalsIgnoreCase)) return true;
            else if (Arrays.stream(FALSE_VALUES).anyMatch(v::equalsIgnoreCase)) return false;
            else throw new IllegalArgumentException("Cannot parse boolean from " + data);
        });
    }

    public static PrimitiveAdapter<Boolean> ofBooleanType() {
        return of(boolean.class, o -> o instanceof Boolean ? (Boolean) o : Boolean.parseBoolean(o.toString()));
    }

    public static PrimitiveAdapter<Character> ofCharacter() {
        return of(Character.class, o -> o instanceof Character ? (Character) o : o.toString().charAt(0));
    }

    public static PrimitiveAdapter<Character> ofCharacterType() {
        return of(char.class, o -> o instanceof Character ? (Character) o : o.toString().charAt(0));
    }

    public static PrimitiveAdapter<Integer> ofInteger() {
        return ofNumber(Integer.class, Number::intValue, Integer::parseInt);
    }

    public static PrimitiveAdapter<Integer> ofIntegerType() {
        return ofNumber(int.class, Number::intValue, Integer::parseInt);
    }

    public static PrimitiveAdapter<Long> ofLong() {
        return ofNumber(Long.class, Number::longValue, Long::parseLong);
    }

    public static PrimitiveAdapter<Long> ofLongType() {
        return ofNumber(long.class, Number::longValue, Long::parseLong);
    }

    public static PrimitiveAdapter<Double> ofDouble() {
        return ofNumber(Double.class, Number::doubleValue, Double::parseDouble);
    }

    public static PrimitiveAdapter<Double> ofDoubleType() {
        return ofNumber(double.class, Number::doubleValue, Double::parseDouble);
    }

    public static PrimitiveAdapter<Float> ofFloat() {
        return ofNumber(Float.class, Number::floatValue, Float::parseFloat);
    }

    public static PrimitiveAdapter<Float> ofFloatType() {
        return ofNumber(float.class, Number::floatValue, Float::parseFloat);
    }

    public static PrimitiveAdapter<Short> ofShort() {
        return ofNumber(Short.class, Number::shortValue, Short::parseShort);
    }

    public static PrimitiveAdapter<Short> ofShortType() {
        return ofNumber(short.class, Number::shortValue, Short::parseShort);
    }

    public static PrimitiveAdapter<Byte> ofByte() {
        return ofNumber(Byte.class, Number::byteValue, Byte::parseByte);
    }

    public static PrimitiveAdapter<Byte> ofByteType() {
        return ofNumber(byte.class, Number::byteValue, Byte::parseByte);
    }

    public static <T> PrimitiveAdapter<T> of(@NotNull Class<T> clazz,
                                             @NotNull DataFunction<Object, T> function) {
        return new PrimitiveAdapter<>(clazz, (p, type, data) -> function.handle(data));
    }

    public static <T extends Number> PrimitiveAdapter<T> ofNumber(@NotNull Class<T> numberClass,
                                                                  @NotNull DataFunction<Number, T> castFunction,
                                                                  @NotNull DataFunction<String, T> parseFunction) {
        return of(numberClass, o -> o instanceof Number ? castFunction.handle((Number) o) : parseFunction.handle(o.toString()));
    }

    protected PrimitiveAdapter(@NotNull Class<T> valueType, @NotNull ValueParser<T> deserializer) {
        super(ValueType.of(valueType), (provider, type, value) -> value, deserializer);
    }

}
