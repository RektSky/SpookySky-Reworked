package ml.rektsky.spookysky.utils

import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodNode

object MappingUtils {

    fun hasString(methodNode: MethodNode, text: String): Boolean {
        for (i in 0 until methodNode.instructions.size()) {
            val abstractInsnNode: AbstractInsnNode = methodNode.instructions.get(i)
            if (abstractInsnNode is LdcInsnNode) {
                if ((abstractInsnNode as LdcInsnNode).cst is String) {
                    if (((abstractInsnNode as LdcInsnNode).cst as String).contains(text)) {
                        return true
                    }
                }
            }
            if (abstractInsnNode is InvokeDynamicInsnNode) {
                val invokeDynamic: InvokeDynamicInsnNode = abstractInsnNode as InvokeDynamicInsnNode
                for (bsmArg in invokeDynamic.bsmArgs) {
                    if (bsmArg is String) {
                        if ((bsmArg as String).contains(text)) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

}