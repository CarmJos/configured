package cc.carm.lib.configuration.validators;

import cc.carm.lib.configuration.annotation.ValuePattern;
import cc.carm.lib.configuration.annotation.ValueRange;
import cc.carm.lib.configuration.function.ValueValidator;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public class Validators {

    public static void initialize(ConfigurationHolder<?> holder) {
        holder.initializer().registerValidAnnotation(ValueRange.class, r -> (ho, value) -> {
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("Value is not a number: " + value);
            }

            Number number = (Number) value;
            if (number.doubleValue() < r.min() || number.doubleValue() > r.max()) {
                throw new IllegalArgumentException(r.message());
            }
        });

        holder.initializer().registerValidAnnotation(ValuePattern.class, r -> new ValueValidator<Object>() {
            private final Pattern pattern = Pattern.compile(r.value());

            @Override
            public void validate(@NotNull ConfigurationHolder<?> holder, @Nullable Object value) throws Exception {
                if (value == null) return;
                if (!pattern.matcher(value.toString()).matches()) {
                    throw new IllegalArgumentException(r.message());
                }
            }
        });

    }

}
