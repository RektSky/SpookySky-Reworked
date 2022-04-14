package ml.rektsky.spookysky.mapping

object MethodMappingDelegation {

    fun invoke(mapping: MethodMapping, original: Any?, vararg args: Any?): Any? {
        return mapping.getReflectiveMethod()?.invoke(original, *args)
    }

    fun invokeUnit(mapping: MethodMapping, original: Any?, vararg args: Any?) {
        mapping.getReflectiveMethod()?.invoke(original, *args)
    }

}