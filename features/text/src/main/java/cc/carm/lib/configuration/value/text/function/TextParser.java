package cc.carm.lib.configuration.value.text.function;

import cc.carm.lib.configuration.value.text.data.TextContents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public abstract class TextParser<RECEIVER, SELF extends TextParser<RECEIVER, SELF>>
    extends ContentHandler<RECEIVER, SELF> {

    public abstract TextContents texts();

    /**
     * Parse the texts for the receiver.
     *
     * @param receiver the receiver
     * @return the parsed line
     */
    public List<String> parse(@Nullable RECEIVER receiver) {
        List<String> result = new ArrayList<>();
        handle(receiver, result::add);
        return result;
    }

    /**
     * Parse the texts for the receiver.
     *
     * @param receiver the receiver
     * @param compiler the compiler
     * @param <V>      the type of the message
     * @return the parsed line
     */
    public <V> List<V> parse(@Nullable RECEIVER receiver, @NotNull BiFunction<RECEIVER, String, V> compiler) {
        List<V> result = new ArrayList<>();
        handle(receiver, s -> result.add(compiler.apply(receiver, s)));
        return result;
    }

    /**
     * Parse the texts as a single line for the receiver.
     *
     * @param receiver the receiver
     * @return the parsed line
     */
    public @Nullable String parseLine(@Nullable RECEIVER receiver) {
        if (texts().isEmpty()) return null;
        StringBuilder builder = new StringBuilder();
        handle(receiver, s -> builder.append(s).append(this.lineSeparator));
        // Remove the last line separator, if it exists
        if (builder.length() > 0) builder.delete(builder.length() - this.lineSeparator.length(), builder.length());
        return builder.toString();
    }

    /**
     * Parse the texts as a single line for the receiver.
     *
     * @param receiver the receiver
     * @param <V>      the type of the message
     * @return the parsed line
     */
    public <V> @Nullable V parseLine(@Nullable RECEIVER receiver, @NotNull BiFunction<RECEIVER, String, V> compiler) {
        return Optional.ofNullable(parseLine(receiver)).map(s -> compiler.apply(receiver, s)).orElse(null);
    }

    public void handle(@Nullable RECEIVER receiver, @NotNull Consumer<String> lineConsumer) {
        handle(texts(), receiver, lineConsumer);
    }

}
