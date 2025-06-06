package cc.carm.lib.configuration.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValueRange {

    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;

    String message() default "Value out of range.";

}
