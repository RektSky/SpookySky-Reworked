package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.LdcInsnNode

class PMinecraftMapper: Processor() {
    override fun process0(loadedClass: LoadedClass) {
        for (method in loadedClass.classNode.methods) {
            method.instructions.firstOrNull {it is LdcInsnNode && it.cst == "Couldn't set pixel format"}?.let {
                MapMinecraft.mapped = loadedClass
                return
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.isMapped()
    }
}