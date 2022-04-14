package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.getFieldByName
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode

class MapperDebugFPS: Processor() {

    init {
        dependsOn(MapMinecraft)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (method in loadedClass.classNode.methods) {
            for (withIndex in method.instructions.withIndex()) {
                val instruction = withIndex.value
                if (instruction is LdcInsnNode && instruction.cst == "fps") {
                    MapMinecraft.mapDebugFPS.mapped =
                        loadedClass.classNode.getFieldByName((method.instructions[withIndex.index + 1] as FieldInsnNode).name)
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapDebugFPS.isMapped()
    }
}