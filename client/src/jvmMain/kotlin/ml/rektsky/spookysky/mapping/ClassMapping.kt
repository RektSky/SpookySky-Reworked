package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.ProcessorManager
import kotlin.concurrent.withLock

abstract class ClassMapping(
    userFriendlyName: String,
): Mapping<LoadedClass>(userFriendlyName) {
    val children: ArrayList<Mapping<*>>
        get() {
            val out = ArrayList<Mapping<*>>()
            for (declaredField in javaClass.declaredFields) {
                if (FieldMapping::class.java.isAssignableFrom(declaredField.type) ||
                    MethodMapping::class.java.isAssignableFrom(declaredField.type)) {
                    declaredField.isAccessible = true
                    out.add(declaredField.get(this) as Mapping<*>)
                }
            }
            return out
        }


    fun getReflectiveClass(): Class<*>? {
        return if (mapped == null) null else
            Class.forName(mapped!!.classNode.name.replace("/", "."))
    }

}