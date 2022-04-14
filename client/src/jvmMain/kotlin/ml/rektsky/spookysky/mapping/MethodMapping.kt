package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.utils.DescriptorUtil
import org.objectweb.asm.tree.MethodNode
import java.lang.reflect.Method

open class MethodMapping(
    val parent: ClassMapping,
    userFriendlyName: String
): Mapping<MethodNode>(userFriendlyName) {

    fun getReflectiveMethod(): Method? {
        return if (mapped == null) null else {
            val declaredMethod = parent.getReflectiveClass()!!.getDeclaredMethod(
                mapped!!.name,
                *DescriptorUtil.getParameterTypes(mapped!!.desc).toTypedArray()
            )
            declaredMethod.isAccessible = true
            declaredMethod
        }
    }

}