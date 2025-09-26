package cc.carm.lib.configuration.builder.collection;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.impl.AbstractSectionBuilder;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.impl.CollectionConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SectionCollectionBuilder<
    V, C extends Collection<V>,
    RESULT extends CollectionConfigValue<V, C, ?>,
    SELF extends SectionCollectionBuilder<V, C, RESULT, SELF>
    > extends AbstractSectionBuilder<C, V, RESULT, SELF> {

    protected @NotNull Supplier<? extends C> constructor;

    public SectionCollectionBuilder(@NotNull Supplier<? extends C> constructor,
                                    @NotNull ValueType<V> paramType,
                                    @NotNull ValueHandler<ConfigureSection, V> parser,
                                    @NotNull ValueHandler<V, ? extends Map<String, Object>> serializer) {
        super(new ValueType<C>() {
        }, paramType, parser, serializer);
        this.constructor = constructor;
    }

    @SafeVarargs
    public final @NotNull SELF defaults(@NotNull V... values) {
        return defaults(c -> c.addAll(Arrays.asList(values)));
    }

    public final @NotNull SELF defaults(@NotNull Consumer<C> constructor) {
        return defaults(() -> {
            C collection = this.constructor.get();
            constructor.accept(collection);
            return collection;
        });
    }

    public SELF constructor(@NotNull Supplier<? extends C> constructor) {
        this.constructor = constructor;
        return self();
    }

    public SELF construct(@NotNull C collection) {
        return constructor(() -> collection);
    }

}
