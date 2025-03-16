package cc.carm.lib.configuration.annotation;

import cc.carm.lib.configuration.source.loader.ConfigurationInitializer;

public class Ex {

    static void init(ConfigurationInitializer initializer) {
        initializer.appendFieldInitializer((holder, path, field, value) -> {
            ValueRange range = field.getAnnotation(ValueRange.class);
            if (range == null) return;
            value.validate((h, v) -> {
                if (!(v instanceof Number)) return;
                Number number = (Number) v;
                if (number.doubleValue() >= range.min() && number.doubleValue() <= range.max()) {
                    throw new IllegalArgumentException(range.message());
                }
            });
        });
    }

}
