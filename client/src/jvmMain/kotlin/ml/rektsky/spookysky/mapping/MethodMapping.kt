package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.utils.DescriptorUtil
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Method

abstract class MethodMapping(
    val parent: ClassMapping,
    userFriendlyName: String
): Mapping<MethodNode>(userFriendlyName) {


    fun getReflectiveMethod(): Method? {
        return if (mapped == null) null else
            parent.getReflectiveClass()!!.getDeclaredMethod(mapped!!.name,
                *DescriptorUtil.getParameterTypes(mapped!!.desc).toTypedArray())
    }

}