package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.world.MapWorld
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.getFieldByName
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode

class MapperLoadedEntitiesList: Processor() {

    init {
        dependsOn(MapWorld)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapWorld.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (method in loadedClass.classNode.methods) {
            for (pair in method.instructions.zip(0 until method.instructions.size())) {
                val instruction = pair.first
                val index = pair.second
                if (instruction is LdcInsnNode) {
                    if (instruction.cst == "remove") {
                        val first = method.instructions.withIndex().filter { it.index > index }
                            .first { it.value is FieldInsnNode }
                        MapWorld.mapLoadedEntitiesList.mapped = loadedClass.classNode.getFieldByName((first.value as FieldInsnNode).name)
                        return
                    }
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapWorld.mapLoadedEntitiesList.isMapped()
    }
}