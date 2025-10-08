package cc.carm.lib.configuration.value.text.function.replacer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ContentReplacer<RECEIVER> implements Comparable<ContentReplacer<RECEIVER>> {

    public static <R> ContentReplacer.Builder<R> match(@NotNull Pattern pattern) {
        return new Builder<>(pattern);
    }

    public static <R> ContentReplacer.Builder<R> match(@NotNull String pattern) {
        return match(Pattern.compile(pattern));
    }

    public static <R> ContentReplacer.Builder<R> replace(@NotNull String text) {
        return match(Pattern.quote(text));
    }

    protected final int priority;
    protected @NotNull Pattern pattern;

    public ContentReplacer(int priority, @NotNull Pattern pattern) {
        this.priority = priority;
        this.pattern = pattern;
    }

    public int priority() {
        return priority;
    }

    public void pattern(@NotNull Pattern pattern) {
        this.pattern = pattern;
    }

    public @NotNull Matcher matcher(@NotNull String text) {
        return pattern.matcher(text);
    }

    public String replace(@NotNull String text, @Nullable RECEIVER receiver) {
        Matcher matcher = matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                String replaced = get(receiver, matcher);
                matcher.appendReplacement(sb, replaced == null ? "" : replaced);
            } catch (Exception ex) {
                // Do nothing if exception occurred.
                ex.printStackTrace(); // for debug
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    protected abstract @Nullable String get(@Nullable RECEIVER receiver, @NotNull Matcher matcher);

    @Override
    public int compareTo(@NotNull ContentReplacer<RECEIVER> o) {
        return Integer.compare(o.priority, this.priority);
    }

    public static class Builder<R> {

        protected final @NotNull Pattern pattern;
        protected int priority = 0;

        public Builder(@NotNull Pattern pattern) {
            this.pattern = pattern;
        }

        public @NotNull Builder<R> priority(int priority) {
            this.priority = priority;
            return this;
        }

        public @NotNull ContentReplacer<R> to(BiFunction<@Nullable R, @NotNull Matcher, @Nullable String> supplier) {
            return new ContentReplacer<R>(this.priority, this.pattern) {
                @Override
                protected @Nullable String get(@NotNull R receiver, @NotNull Matcher matcher) {
                    return supplier.apply(receiver, matcher);
                }
            };
        }

        public @NotNull ContentReplacer<R> to(Function<@Nullable R, @Nullable String> supplier) {
            return to((receiver, matchedParam) -> supplier.apply(receiver));
        }

        public @NotNull ContentReplacer<R> to(Supplier<@Nullable String> supplier) {
            return to((receiver, matchedParam) -> supplier.get());
        }

        public @NotNull ContentReplacer<R> to(@NotNull String content) {
            return to(() -> content);
        }

    }

    @FunctionalInterface
    public interface Constructor<R> extends Function<ContentReplacer.Builder<R>, ContentReplacer<R>> {
    }

}
