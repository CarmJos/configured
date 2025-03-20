package cc.carm.lib.configuration.value.text.function.common;

import cc.carm.lib.configuration.value.text.function.replacer.ContentReplacer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamReplacer<RECEIVER> extends ContentReplacer<RECEIVER> {

    public static final @NotNull Pattern DEFAULT_PARAM_PATTERN = Pattern.compile("%\\((?<id>[^)]+)\\)");

    public static <R> ParamReplacer<R> defaults() {
        return create(DEFAULT_PARAM_PATTERN, m -> m.group("id"));
    }

    public static <R> ParamReplacer<R> create(@NotNull Pattern pattern, @NotNull Function<Matcher, String> extractor) {
        return new ParamReplacer<>(0, pattern, extractor);
    }

    protected final @NotNull Function<Matcher, @Nullable String> extractor;
    protected @NotNull Map<String, Object> placeholders = new HashMap<>();

    public ParamReplacer(int priority, @NotNull Pattern pattern, @NotNull Function<Matcher, String> extractor) {
        super(priority, pattern);
        this.extractor = extractor;
    }

    public @NotNull Map<String, Object> placeholders() {
        return placeholders;
    }

    @Override
    protected @Nullable String get(@NotNull RECEIVER receiver, @NotNull Matcher matcher) {
        @Nullable String id = extractor.apply(matcher);
        if (id == null) return null;
        Object value = placeholders.get(id);
        if (value == null) return null;
        return value.toString();
    }

    public void set(@NotNull Map<String, Object> placeholders) {
        this.placeholders = placeholders;
    }

    public void put(@NotNull String id, @Nullable Object value) {
        placeholders.put(id, value);
    }

    public void remove(@NotNull String id) {
        placeholders.remove(id);
    }

    public void putAll(@NotNull Map<String, Object> placeholders) {
        this.placeholders.putAll(placeholders);
    }

    public void putAll(@NotNull String[] params, @NotNull Object[] values) {
        for (int i = 0; i < params.length; i++) {
            placeholders.put(params[i], (values != null && values.length > i) ? values[i] : "?");
        }
    }

    public void clear() {
        placeholders.clear();
    }
}
