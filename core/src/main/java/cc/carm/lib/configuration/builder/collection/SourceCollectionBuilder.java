package cc.carm.lib.configuration.builder.collection;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.impl.AbstractSourceBuilder;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.value.impl.CollectionConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class SourceCollectionBuilder<
    SOURCE, V, C extends Collection<V>,
    RESULT extends CollectionConfigValue<V, C, ?>,
    SELF extends SourceCollectionBuilder<SOURCE, V, C, RESULT, SELF>
    >
    extends AbstractSourceBuilder<C, SOURCE, V, RESULT, SELF> {

    protected @NotNull Supplier<? extends C> constructor;

    public SourceCollectionBuilder(@NotNull Supplier<? extends C> constructor,
                                   @NotNull ValueType<SOURCE> sourceType, @NotNull ValueType<V> paramType,
                                   @NotNull ValueHandler<SOURCE, V> parser, @NotNull ValueHandler<V, SOURCE> serializer) {
        super(new ValueType<C>() {
        }, sourceType, paramType, parser, serializer);
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
