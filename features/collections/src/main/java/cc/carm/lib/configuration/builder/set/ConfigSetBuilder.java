package cc.carm.lib.configuration.builder.set;

import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.builder.collection.SectionCollectionBuilder;
import cc.carm.lib.configuration.builder.collection.SourceCollectionBuilder;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.collections.ConfiguredSet;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ConfigSetBuilder<V> {

    protected final @NotNull ValueType<V> type;

    public ConfigSetBuilder(@NotNull ValueType<V> type) {
        this.type = type;
    }

    public <S> @NotNull SourceBuilder<S, V> from(@NotNull Class<S> sourceType) {
        return from(ValueType.of(sourceType));
    }

    public <S> @NotNull SourceBuilder<S, V> from(@NotNull ValueType<S> sourceType) {
        return new SourceBuilder<>(
            sourceType, type,
            ValueHandler.required(type),
            ValueHandler.required(sourceType)
        );
    }

    public @NotNull ConfigSetBuilder.SourceBuilder<Object, V> fromObject() {
        return new SourceBuilder<>(
            ValueType.OBJECT, type,
            ValueHandler.deserialize(type), ValueHandler.toObject()
        );
    }

    public @NotNull ConfigSetBuilder.SourceBuilder<String, V> fromString() {
        return new SourceBuilder<>(
            ValueType.STRING, type,
            ValueHandler.required(type), ValueHandler.stringValue()
        );
    }

    public @NotNull ConfigSetBuilder.SectionBuilder<V> fromSection() {
        return new SectionBuilder<>(type, ValueHandler.required(type), ValueHandler.required());
    }


    public static class SourceBuilder<SOURCE, V> extends SourceCollectionBuilder<SOURCE, V, Set<V>, ConfiguredSet<V>, SourceBuilder<SOURCE, V>> {

        public SourceBuilder(@NotNull ValueType<SOURCE> sourceType,
                             @NotNull ValueType<V> paramType,
                             @NotNull ValueHandler<SOURCE, V> parser,
                             @NotNull ValueHandler<V, SOURCE> serializer) {
            super(LinkedHashSet::new, sourceType, paramType, parser, serializer);
        }

        @Override
        protected @NotNull ConfigSetBuilder.SourceBuilder<SOURCE, V> self() {
            return this;
        }

        @Override
        public @NotNull ConfiguredSet<V> build() {
            return new ConfiguredSet<>(buildManifest(), constructor, buildAdapter());
        }

    }

    public static class SectionBuilder<V> extends SectionCollectionBuilder<V, Set<V>, ConfiguredSet<V>, SectionBuilder<V>> {

        public SectionBuilder(@NotNull ValueType<V> paramType,
                              @NotNull ValueHandler<ConfigureSection, V> parser,
                              @NotNull ValueHandler<V, ? extends Map<String, Object>> serializer) {
            super(LinkedHashSet::new, paramType, parser, serializer);
        }

        @Override
        protected @NotNull ConfigSetBuilder.SectionBuilder<V> self() {
            return this;
        }

        @Override
        public @NotNull ConfiguredSet<V> build() {
            return new ConfiguredSet<>(buildManifest(), constructor, buildAdapter());
        }

    }


}
