package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.ProcessorManager
import kotlin.concurrent.withLock

abstract class ClassMapping(
    userFriendlyName: String,
    vararg val children: Mapping<*>
): Mapping<LoadedClass>(userFriendlyName) {

    fun getReflectiveClass(): Class<*>? {
        return if (mapped == null) null else
            Class.forName(mapped!!.classNode.name.replace("/", "."))
    }

}