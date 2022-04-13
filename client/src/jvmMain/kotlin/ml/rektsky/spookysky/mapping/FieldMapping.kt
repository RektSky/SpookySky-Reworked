package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.ProcessorManager
import org.objectweb.asm.tree.FieldNode
import java.lang.reflect.Field
import kotlin.concurrent.withLock

open class FieldMapping(
    val parent: ClassMapping,
    userFriendlyName: String
): Mapping<FieldNode>(userFriendlyName) {




    fun getReflectiveField(): Field? {
        return if (mapped == null) null else parent.getReflectiveClass()!!.getDeclaredField(mapped!!.name)
    }

}