package cc.carm.lib.configuration.value.text.function.common;

import cc.carm.lib.configuration.value.text.function.inserter.ContentInserter;
import cc.carm.lib.configuration.value.text.function.inserter.Insertable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class AppendLineInserter<RECEIVER> extends ContentInserter<RECEIVER> {

    /**
     * Used to match the message insertion.
     * <p>
     * format:
     * <br>- to insert parsed line {prefix}#content-id#{offset-above,offset-down}
     * <br>- to insert original line {prefix}@content-id@{offset-above,offset-down}
     * <br>  original lines will not be parsed
     * <br> example:
     * <ul>
     *     <li>{- }#content-id#{1,1}</li>
     *     <li>@content-id@{1,1}</li>
     * </ul>
     */
    public static final @NotNull Pattern APPEND_PATTERN = Pattern.compile(
            "^(?:\\{(?<prefix>.*)})?#(?<id>.*)#(?:\\{(?<above>-?\\d+)(?:,(?<down>-?\\d+))?})?$"
    );

    public AppendLineInserter(int priority) {
        super(priority, APPEND_PATTERN);
    }

    @Override
    protected @Nullable String extractID(@NotNull Matcher matcher) {
        return matcher.group("id");
    }

    @Override
    protected @NotNull List<String> get(@Nullable RECEIVER receiver, @NotNull Matcher matcher,
                                        @NotNull Insertable<RECEIVER, ?> insertion) {
        String id = extractID(matcher);
        List<String> values = insertion.getInsertion(id, receiver);
        if (values == null || values.isEmpty()) return Collections.emptyList(); // No values to insert

        String prefix = Optional.ofNullable(matcher.group("prefix")).orElse("");
        int offsetAbove = Optional.ofNullable(matcher.group("above"))
                .map(Integer::parseInt).orElse(0);
        int offsetDown = Optional.ofNullable(matcher.group("down"))
                .map(Integer::parseInt).orElse(offsetAbove); // If offsetDown is not set, use offsetAbove

        List<String> contents = new ArrayList<>();

        IntStream.range(0, Math.max(0, offsetAbove)).mapToObj(i -> "").forEach(contents::add);
        values.stream().map(value -> prefix + value).forEach(contents::add);
        IntStream.range(0, Math.max(0, offsetDown)).mapToObj(i -> "").forEach(contents::add);

        return contents;
    }


}
