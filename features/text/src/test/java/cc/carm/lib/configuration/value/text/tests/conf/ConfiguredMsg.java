package cc.carm.lib.configuration.value.text.tests.conf;

import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.text.ConfiguredText;
import cc.carm.lib.configuration.value.text.data.TextContents;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public class ConfiguredMsg extends ConfiguredText<String, PrintStream> {

    public static @NotNull MsgBuilder builder() {
        return new MsgBuilder();
    }

    public static @NotNull ConfiguredMsg of(@NotNull String... text) {
        return builder().defaults(text).build();
    }

    public ConfiguredMsg(@NotNull ValueManifest<TextContents, TextContents> manifest,
                         @NotNull String[] params) {
        super(
            manifest,
            (p, s) -> s,
            (p, s) -> s,
            (p, s) -> s.forEach(p::println),
            params
        );
    }

    public void print(@NotNull Object... values) {
        sendTo(System.out, values);
    }

    public static class MsgBuilder extends Builder<String, PrintStream, MsgBuilder> {

        @Override
        protected @NotNull MsgBuilder self() {
            return this;
        }

        @Override
        public @NotNull ConfiguredMsg build() {
            return new ConfiguredMsg(buildManifest(), params);

        }
    }
}
