package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.MappingUtils
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode

const val magicString = "Null returned as 'hitResult', this shouldn't happen!"

class PLeftRightClickMapper: Processor() {

    init {
        dependsOn(MapMinecraft)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val mapped = MapMinecraft.mapped!!
        for (method in loadedClass.classNode.methods) {
            if (MappingUtils.hasString(magicString, method)) {
                var hasFour = false
                for (instruction in method.instructions) {
                    if (instruction is InsnNode && instruction.opcode == Opcodes.ICONST_4) {
                        hasFour = true
                    }
                }
                if (hasFour) {
                    MapMinecraft.mapRightClickMouse.mapped = method
                } else {
                    MapMinecraft.mapClickMouse.mapped = method
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapClickMouse.isMapped() && MapMinecraft.mapRightClickMouse.isMapped()
    }
}