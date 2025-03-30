package cc.carm.lib.configuration.kotlin.value

import cc.carm.lib.configuration.adapter.ValueType
import cc.carm.lib.configuration.builder.value.SourceValueBuilder
import cc.carm.lib.configuration.value.standard.ConfiguredValue
import kotlin.reflect.KClass

inline fun <S : Any, reified T> valueFrom(
    clazz: KClass<S>, block: (SourceValueBuilder<S, T>.() -> Unit)
): ConfiguredValue<T> {
    return valueFrom(clazz.java, block)
}

inline fun <S : Any, reified V> valueFrom(
    clazz: Class<S>, block: (SourceValueBuilder<S, V>.() -> Unit)
): ConfiguredValue<V> {
    return valueFrom(ValueType.of(clazz), block)
}

inline fun <S : Any, reified V> valueFrom(
    valueType: ValueType<S>, block: (SourceValueBuilder<S, V>.() -> Unit)
): ConfiguredValue<V> {
    val configBuilder = ConfiguredValue.builderOf(V::class.java)
    val sourceValueBuilder: SourceValueBuilder<S, V> = if (valueType.rawType == String::class.java) {
        @Suppress("UNCHECKED_CAST")
        configBuilder.fromString() as SourceValueBuilder<S, V>
    } else configBuilder.from(valueType)
    sourceValueBuilder.parse { holder, data ->
        holder.deserialize(V::class.java, data)
    }
    sourceValueBuilder.serialize { holder, data ->
        @Suppress("UNCHECKED_CAST")
        holder.serialize(data) as? S
    }
    return sourceValueBuilder.also(block).build()
}
