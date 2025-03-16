package cc.carm.lib.configuration.value.text;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.AbstractConfigBuilder;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.standard.ConfiguredValue;
import cc.carm.lib.configuration.value.text.data.TextContents;
import cc.carm.lib.configuration.value.text.function.TextDispatcher;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * @param <MSG>      The type of the message
 * @param <RECEIVER> The type of the receiver
 */
public class ConfiguredText<MSG, RECEIVER> extends ConfiguredValue<TextContents> {

    public static <M, R> Builder<M, R, ?> create() {
        return new StardardBuilder<>();
    }

    public static final ValueType<TextContents> TEXT_TYPE = ValueType.of(TextContents.class);
    public static final ValueAdapter<TextContents> TEXT_ADAPTER = new ValueAdapter<>(TEXT_TYPE,
            (h, t, d) -> d.serialize(),
            (h, t, d) -> TextContents.deserialize(d)
    );

    protected final @NotNull BiFunction<RECEIVER, String, String> parser;
    protected final @NotNull BiFunction<RECEIVER, String, MSG> compiler;
    protected final @NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher;

    protected final @NotNull String[] params; // The parameters of the message.

    public ConfiguredText(@NotNull ValueManifest<TextContents, TextContents> manifest,
                          @NotNull BiFunction<RECEIVER, String, String> parser,
                          @NotNull BiFunction<RECEIVER, String, MSG> compiler,
                          @NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher,
                          @NotNull String[] params) {
        super(manifest, TEXT_ADAPTER);
        this.parser = parser;
        this.compiler = compiler;
        this.dispatcher = dispatcher;
        this.params = params;
    }

    public TextDispatcher<MSG, RECEIVER, ?> prepare(@NotNull Object... values) {
        return new PreparedText<MSG, RECEIVER>(resolve(), this.params)
                .parser(this.parser).compiler(this.compiler)
                .dispatcher(this.dispatcher).placeholders(values);
    }

    /**
     * Parse the message for the receiver.
     *
     * @param receiver The receiver of the message.
     * @param values   The values to replace the {@link #params}.
     * @return The parsed message.
     */
    public List<String> parse(@Nullable RECEIVER receiver, @NotNull Object... values) {
        return prepare(values).parse(receiver);
    }

    /**
     * Parse the message for the receiver and send it.
     *
     * @param receiver The receiver of the message.
     * @param values   The values to replace the {@link #params}.
     * @return The parsed message.
     */
    public String parseLine(@Nullable RECEIVER receiver, @NotNull Object... values) {
        return prepare(values).parseLine(receiver);
    }

    /**
     * Compile the message for the receiver.
     *
     * @param receiver The receiver of the message.
     * @param values   The values to replace the {@link #params}.
     * @return The compiled message.
     */
    public List<MSG> compile(@Nullable RECEIVER receiver, @NotNull Object... values) {
        return prepare(values).compile(receiver);
    }

    /**
     * Compile the message for the receiver and send it.
     *
     * @param receiver The receiver of the message.
     * @param values   The values to replace the {@link #params}.
     * @return The compiled message.
     */
    public MSG compileLine(@Nullable RECEIVER receiver, @NotNull Object... values) {
        return prepare(values).compileLine(receiver);
    }

    /**
     * Send the message to the receiver.
     *
     * @param receiver The receiver of the message.
     * @param values   The values to replace the {@link #params}.
     */
    public void sendTo(@NotNull RECEIVER receiver, @NotNull Object... values) {
        prepare(values).to(receiver);
    }

    /**
     * Send the message to the multiple receivers.
     *
     * @param receivers The receivers of the message.
     * @param values    The values to replace the {@link #params}.
     */
    public void sendToAll(@NotNull Iterable<? extends RECEIVER> receivers, @NotNull Object... values) {
        prepare(values).to(receivers);
    }

    public abstract static class Builder<MSG, RECEIVER, SELF extends Builder<MSG, RECEIVER, SELF>>
            extends AbstractConfigBuilder<TextContents, TextContents, ConfiguredText<MSG, RECEIVER>, ConfigurationHolder<?>, SELF> {
        protected @NotNull TextContents.Builder defaultBuilder = TextContents.builder();
        protected @NotNull String[] params = new String[0];

        protected @NotNull BiFunction<RECEIVER, String, String> parser = (r, s) -> s;
        protected @NotNull BiFunction<RECEIVER, String, MSG> compiler = (r, s) -> {
            throw new IllegalStateException("Compiler not supplied.");
        };
        protected @NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher = (r, l) -> {
            throw new IllegalStateException("Dispatcher not supplied.");
        };

        protected Builder() {
            super(ConfigurationHolder.class, TEXT_TYPE);
            defaults(() -> defaultBuilder.build()); // Set the default value from the default builder.
        }

        public @NotNull SELF parser(@NotNull BiFunction<RECEIVER, String, String> parser) {
            this.parser = parser;
            return self();
        }

        public @NotNull SELF compiler(@NotNull BiFunction<RECEIVER, String, MSG> compiler) {
            this.compiler = compiler;
            return self();
        }

        public @NotNull SELF dispatcher(@NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher) {
            this.dispatcher = dispatcher;
            return self();
        }

        public @NotNull SELF params(@NotNull String... params) {
            this.params = params;
            return self();
        }

        public @NotNull SELF defaults(@NotNull Consumer<TextContents.Builder> consumer) {
            consumer.accept(this.defaultBuilder);
            return self();
        }

        public @NotNull SELF defaults(@NotNull String... contents) {
            return defaults(builder -> builder.set(contents));
        }

        public @NotNull SELF defaults(@NotNull Iterable<String> contents) {
            return defaults(builder -> builder.set(contents));
        }

        public @NotNull SELF optional(@NotNull String id, @NotNull String... lines) {
            return defaults(builder -> builder.optional(id, lines));
        }

        public @NotNull SELF optional(@NotNull String id, @NotNull Iterable<String> lines) {
            return defaults(builder -> builder.optional(id, lines));
        }

        @Override
        public @NotNull ConfiguredText<MSG, RECEIVER> build() {
            return new ConfiguredText<>(buildManifest(), this.parser, this.compiler, this.dispatcher, this.params);
        }

    }

    public static class StardardBuilder<MSG, RECEIVER> extends Builder<MSG, RECEIVER, StardardBuilder<MSG, RECEIVER>> {
        @Override
        protected @NotNull StardardBuilder<MSG, RECEIVER> self() {
            return this;
        }
    }

}
