package cc.carm.lib.configuration.value.text.function.replacer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Replaceable<RECEIVER, SELF> {

    List<ContentReplacer<RECEIVER>> replacers();

    default String applyReplacements(@Nullable RECEIVER receiver, @NotNull String text) {
        for (ContentReplacer<RECEIVER> replacer : replacers()) {
            text = replacer.replace(text, receiver);
        }
        return text;
    }

    SELF replacer(@NotNull ContentReplacer<RECEIVER> replacer);

    default SELF replacer(@NotNull Pattern pattern, @NotNull ContentReplacer.Constructor<RECEIVER> constructor) {
        ContentReplacer.Builder<RECEIVER> builder = ContentReplacer.match(pattern);
        return replacer(constructor.apply(builder));
    }

    default SELF replace(@NotNull Pattern pattern,
                         @NotNull BiFunction<@Nullable RECEIVER, @NotNull Matcher, @Nullable String> supplier) {
        return replacer(pattern, builder -> builder.to(supplier));
    }

    default SELF replace(@NotNull Pattern pattern,
                         @NotNull Function<@Nullable RECEIVER, @Nullable String> supplier) {
        return replacer(pattern, builder -> builder.to(supplier));
    }

    default SELF replace(@NotNull Pattern pattern,
                         @NotNull Supplier<@Nullable String> supplier) {
        return replacer(pattern, builder -> builder.to(supplier));
    }

    default SELF replace(@NotNull Pattern pattern, @NotNull String replacement) {
        return replacer(pattern, builder -> builder.to(replacement));
    }

    default SELF replace(@NotNull String text, @NotNull String replacement) {
        return replacer(Pattern.compile(Pattern.quote(text)), builder -> builder.to(replacement));
    }

    default SELF replace(@NotNull String text, @NotNull Supplier<@Nullable String> supplier) {
        return replacer(Pattern.compile(Pattern.quote(text)), builder -> builder.to(supplier));
    }

    default SELF replace(@NotNull String text,
                         @NotNull Function<@Nullable RECEIVER, @Nullable String> supplier) {
        return replacer(Pattern.compile(Pattern.quote(text)), builder -> builder.to(supplier));
    }

}
