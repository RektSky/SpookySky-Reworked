package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.mapping.mappings.world.World
import kotlin.reflect.KProperty

class FieldMappingDelegation<R>(
    val original: Any,
    val mapping: FieldMapping,
    val wrapperFunction: (Any) -> R? = { it as R },
    val reversedWrapperFunction: (R) -> Any = { it as Any }
) {
    operator fun getValue(instance: Any, property: KProperty<*>): R? {
        if (mapping.getReflectiveField()?.get(original) == null) {
            return null
        }
        return wrapperFunction(mapping.getReflectiveField()?.get(original)!!)
    }

    operator fun setValue(instance: Any, property: KProperty<*>, value: R) {
        mapping.getReflectiveField()?.set(original, reversedWrapperFunction(value))
    }



}