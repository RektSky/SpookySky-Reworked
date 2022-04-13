package ml.rektsky.spookysky.shellcode.impl

import ml.rektsky.spookysky.shellcode.ShellCode
import ml.rektsky.spookysky.utils.DescriptorUtil
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode
import java.lang.reflect.Method
import java.lang.reflect.Modifier


class ShellCodeInvokeMethod(
    val method: Method
): ShellCode() {
    override fun generate(): InsnList {
        val out = InsnList()

        out.add(
            MethodInsnNode(
                if (Modifier.isStatic(method.modifiers)) Opcodes.INVOKESTATIC else if (method.declaringClass.isInterface) Opcodes.INVOKEINTERFACE else Opcodes.INVOKEVIRTUAL,
                method.declaringClass.name.replace(".", "/"),
                method.name,
                DescriptorUtil.getDescriptor(method.returnType, *method.parameterTypes),
                method.declaringClass.isInterface && !Modifier.isStatic(method.modifiers)
            )
        )
        return out
    }
}