package cc.carm.lib.configuration.adapter.strandard;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static cc.carm.lib.configuration.adapter.strandard.PrimitiveAdapter.*;

public interface StandardAdapters {

    @NotNull PrimitiveAdapter<?>[] PRIMITIVES = new PrimitiveAdapter[]{
            ofString(), ofBoolean(), ofBooleanType(), ofCharacter(), ofCharacterType(),
            ofInteger(), ofIntegerType(), ofLong(), ofLongType(), ofDouble(), ofDoubleType(),
            ofFloat(), ofFloatType(), ofShort(), ofShortType(), ofByte(), ofByteType()
    };

    @NotNull ValueAdapter<Enum<?>> ENUMS = PrimitiveAdapter.ofEnum();

    @NotNull ValueAdapter<UUID> UUID = new ValueAdapter<>(
            ValueType.of(UUID.class),
            (provider, type, value) -> value.toString(),
            (provider, type, value) -> java.util.UUID.fromString(value.toString())
    );

    @NotNull ValueAdapter<ConfigureSection> SECTIONS = new ValueAdapter<>(
            ValueType.of(ConfigureSection.class),
            (provider, type, value) -> value,
            (provider, type, value) -> {
                if (value instanceof ConfigureSection) {
                    return (ConfigureSection) value;
                } else throw new IllegalArgumentException("Value is not a ConfigurationSection");
            }
    );

}
