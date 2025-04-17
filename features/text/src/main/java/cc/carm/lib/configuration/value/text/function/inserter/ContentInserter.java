package cc.carm.lib.configuration.value.text.function.inserter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ContentInserter<RECEIVER> implements Comparable<ContentInserter<RECEIVER>> {

    protected final int priority;
    protected @NotNull Pattern pattern;

    public ContentInserter(int priority, @NotNull Pattern pattern) {
        this.priority = priority;
        this.pattern = pattern;
    }

    public int priority() {
        return priority;
    }

    public void setPattern(@NotNull Pattern pattern) {
        this.pattern = pattern;
    }

    public @NotNull Matcher matcher(@NotNull String text) {
        return pattern.matcher(text);
    }

    protected abstract @Nullable String extractID(@NotNull Matcher matcher);

    protected abstract @NotNull List<String> get(@Nullable RECEIVER receiver, @NotNull Matcher matcher,
                                                 @NotNull Insertable<RECEIVER, ?> insertions);

    public @Nullable List<String> handle(@Nullable RECEIVER receiver, @NotNull String line,
                                         @NotNull Insertable<RECEIVER, ?> insertions) {
        Matcher matcher = matcher(line);
        if (!matcher.matches()) return null;
        if (!insertions.inserting(extractID(matcher))) return Collections.emptyList();
        return get(receiver, matcher, insertions);
    }

    @Override
    public int compareTo(@NotNull ContentInserter<RECEIVER> o) {
        return Integer.compare(o.priority, priority);
    }

}
