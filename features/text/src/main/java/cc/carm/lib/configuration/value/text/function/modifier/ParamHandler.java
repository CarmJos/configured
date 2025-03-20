package cc.carm.lib.configuration.value.text.function.modifier;

import java.util.function.Function;
import java.util.regex.Pattern;

public class ParamHandler {

    // %(<ID>)
    public static final Pattern DEFAULT_PARAM_PATTERN = Pattern.compile("%\\((?<id>[^)]+)\\)");
    public static final ParamHandler DEFAULT_PARAM = new ParamHandler(
            s -> {
            },
            s -> {

            }
    )


    protected final Function<String, String> extractor;
    protected final Function<String, String> replacer;

    public ParamHandler(Function<String, String> extractor, Function<String, String> replacer) {
        this.extractor = extractor;
        this.replacer = replacer;
    }
}
