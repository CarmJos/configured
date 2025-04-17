package cc.carm.lib.configuration.value.text.function;

import cc.carm.lib.configuration.value.text.data.TextContents;
import cc.carm.lib.configuration.value.text.function.common.AppendLineInserter;
import cc.carm.lib.configuration.value.text.function.common.OptionalLineInserter;
import cc.carm.lib.configuration.value.text.function.common.ParamReplacer;
import cc.carm.lib.configuration.value.text.function.inserter.ContentInserter;
import cc.carm.lib.configuration.value.text.function.inserter.Insertable;
import cc.carm.lib.configuration.value.text.function.replacer.ContentReplacer;
import cc.carm.lib.configuration.value.text.function.replacer.Replaceable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public abstract class ContentHandler<RECEIVER, SELF extends ContentHandler<RECEIVER, SELF>>
        implements Replaceable<RECEIVER, SELF>, Insertable<RECEIVER, SELF> {

    protected BiFunction<RECEIVER, String, String> parser = (receiver, value) -> value;
    protected String lineSeparator = System.lineSeparator();

    /**
     * Used to store the placeholders of the message
     */
    protected @NotNull ParamReplacer<RECEIVER> paramReplacer = ParamReplacer.defaults();
    protected @NotNull String[] params;

    protected @NotNull List<ContentReplacer<RECEIVER>> replacers = new ArrayList<>();
    protected @NotNull List<ContentInserter<RECEIVER>> inserters = new ArrayList<>();

    /**
     * Used to store the insertion of the message
     */
    protected final @NotNull Map<String, @Nullable Function<RECEIVER, List<String>>> insertion = new HashMap<>();
    protected boolean disableInsertion = false;

    public ContentHandler() {
        inserters.add(new AppendLineInserter<>(0));
        inserters.add(new OptionalLineInserter<>(0));
    }


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

    @Override
    public boolean noneInsertion() {
        return this.insertion.isEmpty();
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
        this.paramReplacer.set(placeholders);
        return self();
    }

    /**
     * Set the placeholders for the text.
     *
     * @param consumer the placeholders
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholders(@NotNull Consumer<Map<String, Object>> consumer) {
        consumer.accept(this.paramReplacer.placeholders());
        return self();
    }

    /**
     * Set the placeholders for the text.
     *
     * @param values The values to replace the {@link #params(String...)}.
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholders(@Nullable Object... values) {
        this.paramReplacer.putAll(this.params, values);
        return self();
    }

    /**
     * Set the placeholder for the text.
     *
     * @param key   the key of the placeholder
     * @param value the value of the placeholder
     * @return the current {@link ContentHandler} instance
     */
    public SELF placeholder(@NotNull String key, @Nullable Object value) {
        this.paramReplacer.put(key, value);
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

    @Override
    public List<ContentReplacer<RECEIVER>> replacers() {
        return this.replacers;
    }

    @Override
    public SELF replacer(@NotNull ContentReplacer<RECEIVER> replacer) {
        this.replacers.add(replacer);
        this.replacers.sort(ContentReplacer::compareTo);
        return self();
    }

    public SELF paramReplacer(@NotNull ParamReplacer<RECEIVER> paramReplacer) {
        this.paramReplacer = paramReplacer;
        return self();
    }

    @Override
    public List<ContentInserter<RECEIVER>> inserters() {
        return this.inserters;
    }

    @Override
    public SELF inserter(@NotNull ContentInserter<RECEIVER> inserter) {
        this.inserters.add(inserter);
        this.inserters.sort(ContentInserter::compareTo);
        return self();
    }

    @Override
    public SELF insert(@NotNull String id, @Nullable Function<RECEIVER, List<String>> supplier) {
        this.insertion.put(id, supplier);
        return self();
    }

    @Override
    public boolean inserting(@NotNull String id) {
        return this.insertion.keySet().stream().anyMatch(key -> key.equalsIgnoreCase(id));
    }

    @Override
    public @Nullable List<String> getInsertion(@NotNull String id, @Nullable RECEIVER receiver) {
        Function<RECEIVER, List<String>> function = this.insertion.get(id);
        if (function == null) return null;
        return function.apply(receiver);
    }

    @Override
    public SELF removeInsert(@NotNull String id) {
        this.insertion.remove(id);
        return self();
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
        text = applyReplacements(receiver, text); // First, apply the custom replacements
        text = this.paramReplacer.replace(text, receiver); // Then, apply the static placeholders
        return this.parser.apply(receiver, text); // Finally, parse the text
    }

    public void handle(@NotNull TextContents contents, @Nullable RECEIVER receiver,
                       @NotNull Consumer<String> lineConsumer) {
        if (contents.isEmpty()) return; // Nothing to parse

        if (this.disableInsertion || noneInsertion()) {
            contents.lines().forEach(line -> lineConsumer.accept(parse(receiver, line)));
            return; // Simple parsed
        }

        // Set the default insertion of the text
        for (Map.Entry<String, @Nullable Function<RECEIVER, List<String>>> entry : this.insertion.entrySet()) {
            if (entry.getValue() != null) continue;
            List<String> lines = contents.optionalLines().get(entry.getKey());
            if (lines == null) continue;
            entry.setValue(r -> lines);
        }

        lines:
        for (String line : contents.lines()) {
            for (ContentInserter<RECEIVER> inserter : inserters) {
                List<String> inserted = inserter.handle(receiver, line, this);
                if (inserted != null) { // Found the insertion
                    inserted.forEach(l -> lineConsumer.accept(parse(receiver, l)));
                    continue lines; // Go to the next line
                }
            }
            lineConsumer.accept(parse(receiver, line));
        }
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
