package cc.carm.lib.configuration.builder.list;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.impl.AbstractSectionBuilder;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.standard.ConfiguredList;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SectionListBuilder<V>
        extends AbstractSectionBuilder<List<V>, V, ConfiguredList<V>, SectionListBuilder<V>> {

    protected @NotNull Supplier<? extends List<V>> constructor;

    public SectionListBuilder(@NotNull Supplier<? extends List<V>> constructor,
                              @NotNull ValueType<V> paramType,
                              @NotNull ValueHandler<ConfigureSection, V> parser,
                              @NotNull ValueHandler<V, ? extends Map<String, Object>> serializer) {
        super(new ValueType<List<V>>() {
        }, paramType, parser, serializer);
        this.constructor = constructor;
    }

    @SafeVarargs
    public final @NotNull SectionListBuilder<V> defaults(@NotNull V... values) {
        return defaults(new ArrayList<>(Arrays.asList(values)));
    }

    public final @NotNull SectionListBuilder<V> defaults(@NotNull Collection<V> values) {
        return defaults(new ArrayList<>(values));
    }

    public final @NotNull SectionListBuilder<V> defaults(@NotNull Consumer<List<V>> constructor) {
        return defaults(() -> {
            List<V> list = new ArrayList<>();
            constructor.accept(list);
            return list;
        });
    }

    public SectionListBuilder<V> constructor(@NotNull Supplier<? extends List<V>> constructor) {
        this.constructor = constructor;
        return this;
    }

    public <LIST extends List<V>> SectionListBuilder<V> construct(@NotNull LIST list) {
        return constructor(() -> list);
    }

    @Override
    protected @NotNull SectionListBuilder<V> self() {
        return this;
    }

    @Override
    public @NotNull ConfiguredList<V> build() {
        return new ConfiguredList<>(buildManifest(), constructor, buildAdapter());
    }
}
