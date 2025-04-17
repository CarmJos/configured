package cc.carm.lib.configuration.value.text.function.common;

import cc.carm.lib.configuration.value.text.function.inserter.ContentInserter;
import cc.carm.lib.configuration.value.text.function.inserter.Insertable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionalLineInserter<RECEIVER> extends ContentInserter<RECEIVER> {

    /**
     * Used to match the message which can be inserted
     * <p>
     * format:
     * <br>- ?[id]Message content
     * <br> example:
     * <ul>
     *     <li>?[click]Click to use this item!</li>
     * </ul>
     */
    public static final @NotNull Pattern OPTIONAL_PATTERN = Pattern.compile(
            "^\\?\\[(?<id>.+)](?<content>.*)$"
    );

    public OptionalLineInserter(int priority) {
        super(priority, OPTIONAL_PATTERN);
    }

    @Override
    protected @Nullable String extractID(@NotNull Matcher matcher) {
        return matcher.group("id");
    }

    @Override
    protected @NotNull List<String> get(@Nullable RECEIVER receiver, @NotNull Matcher matcher,
                                        @NotNull Insertable<RECEIVER, ?> insertion) {
        String content = matcher.group("content");
        if (content == null) return Collections.emptyList();
        return Collections.singletonList(content);
    }

}
