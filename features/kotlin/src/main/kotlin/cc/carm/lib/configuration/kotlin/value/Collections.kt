package cc.carm.lib.configuration.kotlin.value

import cc.carm.lib.configuration.adapter.ValueType
import cc.carm.lib.configuration.builder.collection.SimpleCollectionCreator
import cc.carm.lib.configuration.builder.set.ConfigSetBuilder
import cc.carm.lib.configuration.value.collections.ConfiguredSet
import cc.carm.lib.configuration.value.standard.ConfiguredList
import kotlin.reflect.KClass

inline fun <S : Any, reified V> listFrom(
    clazz: KClass<S>, block: (SimpleCollectionCreator.Source<S, V, List<V>, ConfiguredList<V>>.() -> Unit)
): ConfiguredList<V> {
    return listFrom(clazz.java, block)
}

inline fun <S : Any, reified V> listFrom(
    clazz: Class<S>, block: (SimpleCollectionCreator.Source<S, V, List<V>, ConfiguredList<V>>.() -> Unit)
): ConfiguredList<V> {
    return listFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified V> listFrom(
    valueType: ValueType<S>, block: (SimpleCollectionCreator.Source<S, V, List<V>, ConfiguredList<V>>.() -> Unit)
): ConfiguredList<V> {
    val configBuilder = ConfiguredList.builderOf(V::class.java)
    val sourceValueBuilder: SimpleCollectionCreator.Source<S, V, List<V>, ConfiguredList<V>> =
        if (valueType.rawType == String::class.java) {
            @Suppress("UNCHECKED_CAST")
            configBuilder.fromString() as SimpleCollectionCreator.Source<S, V, List<V>, ConfiguredList<V>>
        } else configBuilder.from(valueType)
    return sourceValueBuilder.also(block).build()
}

inline fun <S : Any, reified V> setFrom(
    clazz: KClass<S>, block: (ConfigSetBuilder.SourceBuilder<S, V>.() -> Unit)
): ConfiguredSet<V> {
    return setFrom(clazz.java, block)
}

inline fun <S : Any, reified V> setFrom(
    clazz: Class<S>, block: (ConfigSetBuilder.SourceBuilder<S, V>.() -> Unit)
): ConfiguredSet<V> {
    return setFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified V> setFrom(
    valueType: ValueType<S>, block: (ConfigSetBuilder.SourceBuilder<S, V>.() -> Unit)
): ConfiguredSet<V> {
    val configBuilder = ConfiguredSet.builderOf(V::class.java)
    val sourceValueBuilder: ConfigSetBuilder.SourceBuilder<S, V> =
        if (valueType.rawType == String::class.java) {
            @Suppress("UNCHECKED_CAST")
            configBuilder.fromString() as ConfigSetBuilder.SourceBuilder<S, V>
        } else configBuilder.from(valueType)
    return sourceValueBuilder.also(block).build()
}
