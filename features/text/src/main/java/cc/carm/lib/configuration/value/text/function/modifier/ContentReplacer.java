package cc.carm.lib.configuration.value.text.function.modifier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class ContentReplacer<RECEIVER> {

    public static <R> ContentReplacer.Builder<R> match(@NotNull Predicate<String> matcher) {
        return new Builder<>(matcher);
    }

    public static <R> ContentReplacer.Builder<R> match(@NotNull String id) {
        return match(text -> text.equalsIgnoreCase(id));
    }

    public static <R> ContentReplacer.Builder<R> match(@NotNull Pattern pattern) {
        return match(text -> pattern.matcher(text).find());
    }

    protected final int priority;
    protected final @NotNull Predicate<String> matcher;
    protected final BiFunction<@NotNull RECEIVER, @NotNull String, @Nullable String> supplier;

    public ContentReplacer(int priority,
                           @NotNull Predicate<String> matcher,
                           BiFunction<@NotNull RECEIVER, @NotNull String, @Nullable String> supplier) {
        this.priority = priority;
        this.matcher = matcher;
        this.supplier = supplier;
    }

    public @NotNull Predicate<String> matcher() {
        return this.matcher;
    }

    public boolean check(@NotNull String param) {
        return this.matcher.test(param);
    }

    public @Nullable String content(@NotNull RECEIVER receiver, @NotNull String matchedParam) {
        return this.supplier == null ? null : this.supplier.apply(receiver, matchedParam);
    }

    public static class Builder<R> {

        protected final @NotNull Predicate<String> matcher;
        protected int priority = 0;

        public Builder(@NotNull Predicate<String> matcher) {
            this.matcher = matcher;
        }

        public @NotNull Builder<R> priority(int priority) {
            this.priority = priority;
            return this;
        }

        public @NotNull ContentReplacer<R> to(BiFunction<@NotNull R, @NotNull String, @Nullable String> supplier) {
            return new ContentReplacer<>(this.priority, this.matcher, supplier);
        }

        public @NotNull ContentReplacer<R> to(Function<@NotNull R, @Nullable String> supplier) {
            return to((receiver, matchedParam) -> supplier.apply(receiver));
        }

        public @NotNull ContentReplacer<R> to(Supplier<@Nullable String> supplier) {
            return to((receiver, matchedParam) -> supplier.get());
        }

        public @NotNull ContentReplacer<R> to(@NotNull String content) {
            return to(() -> content);
        }

    }

}
