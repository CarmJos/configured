package cc.carm.lib.configuration.kotlin.value

import cc.carm.lib.configuration.adapter.ValueType
import cc.carm.lib.configuration.builder.list.SourceListBuilder
import cc.carm.lib.configuration.value.standard.ConfiguredList
import kotlin.reflect.KClass

inline fun <S : Any, reified V> listFrom(
    clazz: KClass<S>, block: (SourceListBuilder<S, V>.() -> Unit)
): ConfiguredList<V> {
    return listFrom(clazz.java, block)
}

inline fun <S : Any, reified V> listFrom(
    clazz: Class<S>, block: (SourceListBuilder<S, V>.() -> Unit)
): ConfiguredList<V> {
    return listFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified V> listFrom(
    valueType: ValueType<S>, block: (SourceListBuilder<S, V>.() -> Unit)
): ConfiguredList<V> {
    val configBuilder = ConfiguredList.builderOf(V::class.java)
    val sourceValueBuilder: SourceListBuilder<S, V> = if (valueType.rawType == String::class.java) {
        @Suppress("UNCHECKED_CAST")
        configBuilder.fromString() as SourceListBuilder<S, V>
    } else configBuilder.from(valueType)
    return sourceValueBuilder.also(block).build()
}