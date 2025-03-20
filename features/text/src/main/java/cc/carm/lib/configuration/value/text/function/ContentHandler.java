package cc.carm.lib.configuration.value.text.function;

import cc.carm.lib.configuration.value.text.data.TextContents;
import cc.carm.lib.configuration.value.text.function.modifier.ContentReplacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public abstract class ContentHandler<RECEIVER, SELF extends ContentHandler<RECEIVER, SELF>> {

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
    public static final @NotNull Pattern INSERT_PATTERN = Pattern.compile(
            "^(?:\\{(?<prefix>.*)})?(?<type>[#@])(?<id>.*)[#@](?:\\{(?<above>-?\\d+)(?:,(?<down>-?\\d+))?})?$"
    );

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
    public static final @NotNull Pattern ENABLE_PATTERN = Pattern.compile(
            "^\\?\\[(?<id>.+)](?<content>.*)$"
    );

    public static final @NotNull Function<String, String>
    public static final @NotNull UnaryOperator<String> DEFAULT_PARAM_BUILDER = s -> "%(" + s + ")";

    protected BiFunction<RECEIVER, String, String> parser = (receiver, value) -> value;
    protected String lineSeparator = System.lineSeparator();

    /**
     * Used to store the placeholders of the message
     */
    protected @NotNull Map<String, Object> placeholders = new HashMap<>();
    protected @NotNull UnaryOperator<String> paramFormatter = DEFAULT_PARAM_BUILDER;
    protected @NotNull String[] params;

    protected @NotNull List<ContentReplacer> replacers = new ArrayList<>();
    protected @NotNull List<ContentReplacer> inserters = new ArrayList<>();

    /**
     * Used to store the insertion of the message
     */
    protected @NotNull Map<String, @Nullable Function<RECEIVER, List<String>>> insertion = new HashMap<>();
    protected boolean disableInsertion = false;

    public abstract SELF self();

    /**
     * Disable the insertion of the text.
     * <br> If the insertion is disabled, the text will be parsed directly.
     *
     * @return the current {@link ContentHandler} instance
     */
    public SELF disableInsertion() {
        this.disableInsertion = true;
        return self();
    }

    /**
     * Enable the insertion of the text.
     *
     * @return the current {@link ContentHandler} instance
     */
    public SELF enableInsertion() {
        this.disableInsertion = false;
        return self();
    }

    /**
     * Set the line separator for the text.
     *
     * @param lineSeparator the line separator, default is {@link System#lineSeparator()}
     * @return the current {@link ContentHandler} instance
     */
    public SELF lineSeparator(@NotNull String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return self();
    }

    /**
     * Set all the placeholders for the text.
     * <br> Will override the previous placeholders modifications.
     *
     * @param placeholders the placeholders
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholders(@NotNull Map<String, Object> placeholders) {
        this.placeholders = placeholders;
        return self();
    }

    /**
     * Set the placeholders for the text.
     *
     * @param consumer the placeholders
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholders(@NotNull Consumer<Map<String, Object>> consumer) {
        consumer.accept(this.placeholders);
        return self();
    }

    /**
     * Set the placeholders for the text.
     *
     * @param values The values to replace the {@link #params(String...)}.
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholders(@Nullable Object... values) {
        return placeholders(map -> map.putAll(buildParams(this.paramFormatter, this.params, values)));
    }

    /**
     * Set the placeholder for the text.
     *
     * @param key   the key of the placeholder
     * @param value the value of the placeholder
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholder(@NotNull String key, @Nullable Object value) {
        this.placeholders.put(paramFormatter.apply(key), value);
        return self();
    }

    /**
     * Set the params for the text,
     * used for {@link #placeholders(Object...)} to build the placeholders.
     *
     * @param params the params
     * @return the current {@link ContentHandler} instance
     */
    public SELF params(@NotNull String... params) {
        this.params = params;
        return self();
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id the id of the insertion text
     * @return the current {@link ContentHandler} instance
     */
    public SELF insert(@NotNull String id) {
        this.insertion.put(id, null);
        return self();
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id            the id of the insertion text
     * @param linesSupplier to supply the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    public SELF insert(@NotNull String id, @NotNull Function<RECEIVER, List<String>> linesSupplier) {
        this.insertion.put(id, linesSupplier);
        return self();
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id    the id of the insertion text
     * @param lines the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    public SELF insert(@NotNull String id, @NotNull String... lines) {
        return insert(id, Arrays.asList(lines));
    }

    /**
     * Insert the specific contents by the id.
     *
     * @param id    the id of the insertion text
     * @param lines the lines to insert
     * @return the current {@link ContentHandler} instance
     */
    public SELF insert(@NotNull String id, @NotNull List<String> lines) {
        return insert(id, receiver -> lines);
    }

    /**
     * Set the parser for the text.
     *
     * @param parser The parser
     * @return The current {@link ContentHandler} instance
     */
    public SELF parser(@NotNull BiFunction<RECEIVER, String, String> parser) {
        this.parser = parser;
        return self();
    }

    /**
     * Parse the supplied single text for the receiver.
     *
     * @param receiver the receiver
     * @param text     the text to parse
     * @return the parsed text
     */
    protected @Nullable String parse(@Nullable RECEIVER receiver, @NotNull String text) {
        return this.parser.apply(receiver, setPlaceholders(text, this.placeholders));
    }

    public void handle(@NotNull TextContents contents, @Nullable RECEIVER receiver,
                       @NotNull Consumer<String> lineConsumer) {
        if (contents.isEmpty()) return; // Nothing to parse

        if (this.disableInsertion) {
            contents.lines().forEach(line -> lineConsumer.accept(parse(receiver, line)));
            return; // Simple parsed
        }

        for (String line : contents.lines()) {
            Matcher insertMatcher = INSERT_PATTERN.matcher(line);
            if (insertMatcher.matches()) {
                doInsert(insertMatcher, receiver, lineConsumer);
                continue;
            }

            Matcher enableMatcher = ENABLE_PATTERN.matcher(line);
            if (enableMatcher.matches()) {
                if (this.insertion.containsKey(enableMatcher.group("id"))) {
                    lineConsumer.accept(parse(receiver, enableMatcher.group("content")));
                }
                continue;
            }

            lineConsumer.accept(parse(receiver, line));
        }
    }

    private void doInsert(Matcher matcher, @Nullable RECEIVER receiver,
                          @NotNull Consumer<String> lineConsumer) {
        String id = matcher.group("id");
        List<String> values = Optional.ofNullable(this.insertion.get(id))
                .map(f -> f.apply(receiver))
                .orElse(null);
        if (values == null || values.isEmpty()) return; // No values to insert

        String prefix = matcher.group("prefix");
        String type = matcher.group("type");
        boolean original = type.equals("@");
        int offsetAbove = Optional.ofNullable(matcher.group("above"))
                .map(Integer::parseInt).orElse(0);
        int offsetDown = Optional.ofNullable(matcher.group("down"))
                .map(Integer::parseInt).orElse(offsetAbove); // If offsetDown is not set, use offsetAbove

        IntStream.range(0, Math.max(0, offsetAbove)).mapToObj(i -> "").forEach(lineConsumer);
        String prefixContent = Optional.ofNullable(prefix).map(p -> parse(receiver, p)).orElse("");
        if (original) {
            values.stream().map(value -> prefixContent + value).forEach(lineConsumer);
        } else {
            values.stream().map(value -> prefixContent + parse(receiver, value)).forEach(lineConsumer);
        }
        IntStream.range(0, Math.max(0, offsetDown)).mapToObj(i -> "").forEach(lineConsumer);
    }

    public static String setPlaceholders(@NotNull String messages,
                                         @NotNull Map<String, Object> placeholders) {
        if (messages.isEmpty()) return messages;
        String parsed = messages;
        for (Map.Entry<String, Object> entry : placeholders.entrySet()) {
            parsed = parsed.replace(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
        }
        return parsed;
    }

    public static Map<String, Object> buildParams(@NotNull UnaryOperator<String> paramBuilder,
                                                  @Nullable String[] params, @Nullable Object[] values) {
        Map<String, Object> map = new HashMap<>();
        if (params == null || params.length == 0) return map;
        for (int i = 0; i < params.length; i++) {
            map.put(paramBuilder.apply(params[i]), (values != null && values.length > i) ? values[i] : "?");
        }
        return map;
    }

}
