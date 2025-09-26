package cc.carm.lib.configuration.builder.collection;

import cc.carm.lib.configuration.adapter.ValueAdapter;
import cc.carm.lib.configuration.adapter.ValueType;
import cc.carm.lib.configuration.function.ValueHandler;
import cc.carm.lib.configuration.source.section.ConfigureSection;
import cc.carm.lib.configuration.value.ValueManifest;
import cc.carm.lib.configuration.value.impl.CollectionConfigValue;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

public class SimpleCollectionCreator<V, C extends Collection<V>, RESULT extends CollectionConfigValue<V, C, ?>> {

    public static <V, C extends Collection<V>, RESULT extends CollectionConfigValue<V, C, ?>>
    @NotNull SimpleCollectionCreator<V, C, RESULT> create(
        @NotNull ValueType<V> type,
        @NotNull Supplier<? extends C> defaultConstructor,
        @NotNull CollectionValueFactory<V, C, RESULT> factory) {
        return new SimpleCollectionCreator<>(type, defaultConstructor, factory);
    }

    protected final @NotNull Supplier<? extends C> defaultConstructor;
    protected final @NotNull ValueType<V> type;

    protected final @NotNull CollectionValueFactory<V, C, RESULT> factory;

    public SimpleCollectionCreator(@NotNull ValueType<V> type,
                                   @NotNull Supplier<? extends C> defaultConstructor,
                                   @NotNull CollectionValueFactory<V, C, RESULT> factory) {
        this.defaultConstructor = defaultConstructor;
        this.type = type;
        this.factory = factory;
    }

    public <S> @NotNull Source<S, V, C, RESULT> from(@NotNull Class<S> sourceType) {
        return from(ValueType.of(sourceType));
    }

    public <S> @NotNull Source<S, V, C, RESULT> from(@NotNull ValueType<S> sourceType) {
        return new Source<S, V, C, RESULT>(
            defaultConstructor, sourceType, type,
            ValueHandler.required(type),
            ValueHandler.required(sourceType),
            factory
        );
    }

    public @NotNull SimpleCollectionCreator.Source<Object, V, C, RESULT> fromObject() {
        return new Source<Object, V, C, RESULT>(
            defaultConstructor, ValueType.OBJECT, type,
            ValueHandler.deserialize(type), ValueHandler.toObject(),
            factory
        );
    }

    public @NotNull SimpleCollectionCreator.Source<String, V, C, RESULT> fromString() {
        return new Source<String, V, C, RESULT>(
            defaultConstructor, ValueType.STRING, type,
            ValueHandler.required(type), ValueHandler.stringValue(),
            factory
        );
    }

    public @NotNull SimpleCollectionCreator.Section<V, C, RESULT> fromSection() {
        return new Section<V, C, RESULT>(
            defaultConstructor, type,
            ValueHandler.required(type), ValueHandler.required(),
            factory
        );
    }

    @FunctionalInterface
    public interface CollectionValueFactory<V, C, RESULT> {
        @NotNull RESULT build(
            @NotNull ValueManifest<C, V> manifest,
            @NotNull Supplier<? extends C> constructor,
            @NotNull ValueAdapter<V> paramAdapter
        );
    }

    public static class Source<SOURCE, V, C extends Collection<V>, RESULT extends CollectionConfigValue<V, C, ?>>
        extends SourceCollectionBuilder<SOURCE, V, C, RESULT, Source<SOURCE, V, C, RESULT>> {

        protected final @NotNull CollectionValueFactory<V, C, RESULT> factory;

        public Source(
            @NotNull Supplier<? extends C> constructor,
            @NotNull ValueType<SOURCE> sourceType,
            @NotNull ValueType<V> paramType,
            @NotNull ValueHandler<SOURCE, V> parser,
            @NotNull ValueHandler<V, SOURCE> serializer,
            @NotNull CollectionValueFactory<V, C, RESULT> factory) {
            super(constructor, sourceType, paramType, parser, serializer);
            this.factory = factory;
        }

        @Override
        protected @NotNull SimpleCollectionCreator.Source<SOURCE, V, C, RESULT> self() {
            return this;
        }

        @Override
        public @NotNull RESULT build() {
            return factory.build(buildManifest(), constructor, buildAdapter());
        }

    }

    public static class Section<V, C extends Collection<V>, RESULT extends CollectionConfigValue<V, C, ?>>
        extends SectionCollectionBuilder<V, C, RESULT, Section<V, C, RESULT>> {
        protected final @NotNull CollectionValueFactory<V, C, RESULT> factory;

        public Section(
            @NotNull Supplier<? extends C> constructor,
            @NotNull ValueType<V> paramType,
            @NotNull ValueHandler<ConfigureSection, V> parser,
            @NotNull ValueHandler<V, ? extends Map<String, Object>> serializer,
            @NotNull CollectionValueFactory<V, C, RESULT> factory) {
            super(constructor, paramType, parser, serializer);
            this.factory = factory;
        }

        @Override
        protected @NotNull SimpleCollectionCreator.Section<V, C, RESULT> self() {
            return this;
        }

        @Override
        public @NotNull RESULT build() {
            return factory.build(buildManifest(), constructor, buildAdapter());
        }
    }


}
