package ml.rektsky.spookysky.asm

import org.objectweb.asm.ClassWriter

class CustomClassWriter: ClassWriter(ClassWriter.COMPUTE_MAXS) {

    override fun getCommonSuperClass(type1: String?, type2: String?): String {
        return "java/lang/Object"
    }
}