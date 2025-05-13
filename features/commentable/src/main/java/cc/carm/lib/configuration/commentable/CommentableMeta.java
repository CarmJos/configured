package cc.carm.lib.configuration.commentable;

import cc.carm.lib.configuration.annotation.FooterComments;
import cc.carm.lib.configuration.annotation.HeaderComments;
import cc.carm.lib.configuration.annotation.InlineComment;
import cc.carm.lib.configuration.annotation.InlineComments;
import cc.carm.lib.configuration.source.ConfigurationHolder;
import cc.carm.lib.configuration.source.loader.ConfigurationInitializer;
import cc.carm.lib.configuration.source.meta.ConfigurationMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public interface CommentableMeta {

    /**
     * Configuration's {@link HeaderComments}
     *
     * @see HeaderComments
     */
    ConfigurationMetadata<List<String>> HEADER = ConfigurationMetadata.of(Collections.emptyList());

    /**
     * Configuration's footer comments
     *
     * @see FooterComments
     */
    ConfigurationMetadata<List<String>> FOOTER = ConfigurationMetadata.of(Collections.emptyList());

    /**
     * Configuration's {@link InlineComment}
     * <p> Map< regex, comment > , regex is used to match the key, null for current path.
     *
     * @see InlineComment
     */
    ConfigurationMetadata<Map<String, String>> INLINE = ConfigurationMetadata.of();


    static void register(@NotNull ConfigurationHolder<?> provider) {
        register(provider.initializer());
    }

    static void register(@NotNull ConfigurationInitializer initializer) {
        initializer.registerAnnotation(
            HeaderComments.class, HEADER,
            a -> Arrays.asList(a.value())
        );
        initializer.registerAnnotation(
            FooterComments.class, FOOTER,
            a -> Arrays.asList(a.value())
        );
        initializer.registerAnnotation(InlineComment.class, INLINE, a -> {
            Map<String, String> map = new HashMap<>();
            if (a.regex().length == 0) { // for current path
                map.put(null, a.value());
                return map;
            }
            for (String regex : a.regex()) { // for specified path
                map.put(regex, a.value());
            }
            return map;
        });
        initializer.registerAnnotation(InlineComments.class, INLINE, a -> {
            Map<String, String> map = new HashMap<>();
            for (InlineComment comment : a.value()) {
                if (comment.regex().length == 0) { // for current path
                    map.put(null, comment.value());
                    continue;
                }
                for (String regex : comment.regex()) { // for specified path
                    map.put(regex, comment.value());
                }
            }
            return map;
        });
    }

}
