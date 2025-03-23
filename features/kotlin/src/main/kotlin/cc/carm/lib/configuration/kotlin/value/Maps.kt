package cc.carm.lib.configuration.kotlin.value

import cc.carm.lib.configuration.adapter.ValueType
import cc.carm.lib.configuration.builder.map.SourceMapBuilder
import cc.carm.lib.configuration.value.standard.ConfiguredMap
import java.util.*
import kotlin.reflect.KClass

inline fun <S : Any, reified K, reified V> hashmapFrom(
    clazz: KClass<S>,
    block: SourceMapBuilder<HashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return hashmapFrom(clazz.java, block)
}

inline fun <S : Any, reified K, reified V> hashmapFrom(
    clazz: Class<S>,
    block: SourceMapBuilder<HashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return hashmapFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified K, reified V> hashmapFrom(
    valueType: ValueType<S>,
    block: SourceMapBuilder<HashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    val mapCreator = ConfiguredMap.builderOf(K::class.java, V::class.java)
    val sourceValueBuilder: SourceMapBuilder<HashMap<K, V>, S, K, V> = mapCreator.asHashMap().from(valueType)
    return sourceValueBuilder.also(block).build()
}

inline fun <S : Any, reified K, reified V> linkedMapFrom(
    clazz: KClass<S>,
    block: SourceMapBuilder<LinkedHashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return linkedMapFrom(clazz.java, block)
}

inline fun <S : Any, reified K, reified V> linkedMapFrom(
    clazz: Class<S>,
    block: SourceMapBuilder<LinkedHashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return linkedMapFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified K, reified V> linkedMapFrom(
    valueType: ValueType<S>,
    block: SourceMapBuilder<LinkedHashMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    val mapCreator = ConfiguredMap.builderOf(K::class.java, V::class.java)
    val sourceValueBuilder: SourceMapBuilder<LinkedHashMap<K, V>, S, K, V> = mapCreator.asLinkedMap().from(valueType)
    return sourceValueBuilder.also(block).build()
}

inline fun <S : Any, reified K, reified V> treeMapFrom(
    clazz: KClass<S>,
    block: SourceMapBuilder<TreeMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return treeMapFrom(clazz.java, block)
}

inline fun <S : Any, reified K, reified V> treeMapFrom(
    clazz: Class<S>,
    block: SourceMapBuilder<TreeMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return treeMapFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified K, reified V> treeMapFrom(
    valueType: ValueType<S>,
    block: SourceMapBuilder<TreeMap<K, V>, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    val mapCreator = ConfiguredMap.builderOf(K::class.java, V::class.java)
    val sourceValueBuilder: SourceMapBuilder<TreeMap<K, V>, S, K, V> = mapCreator.asTreeMap().from(valueType)
    return sourceValueBuilder.also(block).build()
}

inline fun <reified MAP : Map<K, V>, S : Any, reified K, reified V> mapFrom(
    clazz: KClass<S>,
    noinline map: () -> MAP,
    block: SourceMapBuilder<MAP, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return mapFrom(clazz.java, map, block)
}

inline fun <reified MAP : Map<K, V>, S : Any, reified K, reified V> mapFrom(
    clazz: Class<S>,
    noinline map: () -> MAP,
    block: SourceMapBuilder<MAP, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    return mapFrom(ValueType.of(clazz), map, block)
}

inline fun <reified MAP : Map<K, V>, S : Any, reified K, reified V> mapFrom(
    valueType: ValueType<S>,
    noinline map: () -> MAP,
    block: SourceMapBuilder<MAP, S, K, V>.() -> Unit
): ConfiguredMap<K, V> {
    val mapCreator = ConfiguredMap.builderOf(K::class.java, V::class.java)
    val sourceValueBuilder: SourceMapBuilder<MAP, S, K, V> = mapCreator.constructor(map).from(valueType)
    return sourceValueBuilder.also(block).build()
}


fun <MAP : Map<K, V>, S, K, V> SourceMapBuilder<MAP, S, K, V>.defaultMap(map: MAP): SourceMapBuilder<MAP, S, K, V> {
    return defaults(map)
}