package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.MappingUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode

const val magicString = "Null returned as 'hitResult', this shouldn't happen!"

class PLeftRightClickMapper: Processor() {

    init {
        dependsOn(Minecraft)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == Minecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val mapped = Minecraft.mapped!!
        for (method in loadedClass.classNode.methods) {
            if (MappingUtils.hasString(method, magicString)) {
                var hasFour = false
                for (instruction in method.instructions) {
                    if (instruction is InsnNode && instruction.opcode == Opcodes.ICONST_4) {
                        hasFour = true
                    }
                }
                if (hasFour) {
                    Minecraft.mapRightClickMouse.mapped = method
                } else {
                    Minecraft.mapClickMouse.mapped = method
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return Minecraft.mapClickMouse.isMapped() && Minecraft.mapRightClickMouse.isMapped()
    }
}