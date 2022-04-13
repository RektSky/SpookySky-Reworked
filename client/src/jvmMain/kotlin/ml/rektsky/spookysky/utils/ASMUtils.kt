package ml.rektsky.spookysky.utils

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode

object ASMUtils {

    fun ClassNode.compile(): ByteArray {
        val writer: ClassWriter = ClassWriter(ClassWriter.COMPUTE_MAXS)
        accept(writer)
        return writer.toByteArray()
    }

    fun decompile(data: ByteArray): ClassNode {
        val reader: ClassReader = ClassReader(data)
        val classNode = ClassNode()
        reader.accept(classNode, 0)
        return classNode
    }

}

fun Any.getClassName(): String {
    return javaClass.name
}

fun Any.getDescriptorName(): String {
    return javaClass.name.replace(".", "/")
}

fun Any.getBytecodeName(): String {
    return DescriptorUtil.toDescriptorTypeName(javaClass.name)
}