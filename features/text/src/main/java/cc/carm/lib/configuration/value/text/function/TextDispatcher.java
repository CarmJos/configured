package cc.carm.lib.configuration.value.text.function;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class TextDispatcher<MSG, RECEIVER, SELF extends TextDispatcher<MSG, RECEIVER, SELF>> extends TextCompiler<MSG, RECEIVER, SELF> {

    protected @NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher = (receiver, msg) -> {
    };

    /**
     * Set the dispatcher to send the message to the receiver
     *
     * @param dispatcher the dispatcher
     * @return {@link TextDispatcher}
     */
    public SELF dispatcher(@NotNull BiConsumer<RECEIVER, List<MSG>> dispatcher) {
        this.dispatcher = dispatcher;
        return self();
    }


    /**
     * Dispatch the message to the receiver
     *
     * @param receivers the receivers
     */
    @SafeVarargs
    public final void to(@NotNull RECEIVER... receivers) {
        if (receivers.length == 0) return;
        to(Arrays.asList(receivers));
    }

    /**
     * Dispatch the message to the receiver
     *
     * @param receivers the receivers
     */
    public void to(@NotNull Iterable<? extends RECEIVER> receivers) {
        for (RECEIVER receiver : receivers) {
            List<MSG> msg = compile(receiver);
            if (msg.isEmpty()) return;
            dispatcher.accept(receiver, msg);
        }
    }

    /**
     * Dispatch the message to the receiver
     *
     * @param receivers the receivers
     */
    public void to(@NotNull Supplier<Iterable<? extends RECEIVER>> receivers) {
        to(receivers.get());
    }

}
