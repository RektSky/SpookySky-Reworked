package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.settings.MapKeyBinding
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.getFieldByName
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode

class MapperKeyBinding: Processor() {

    init {
        dependsOn(MapKeyBinding)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapKeyBinding.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (method in loadedClass.classNode.methods) {
            if (method.desc == "(IZ)V") {
                MapKeyBinding.mapSetKeyBindState.mapped = method
                MapKeyBinding.mapPressed.mapped = loadedClass.classNode.fields.first {
                    it.name == (method.instructions.firstOrNull { it is FieldInsnNode && it.opcode == Opcodes.PUTFIELD } as FieldInsnNode).name
                }
            }

            val indexOfFirst = method.instructions.indexOfFirst { it.opcode == Opcodes.IADD }
            if (indexOfFirst != -1) {
                MapKeyBinding.mapPressTime.mapped = loadedClass.classNode
                    .getFieldByName((method.instructions[indexOfFirst + 1] as FieldInsnNode).name)
            }
            if (method.name == "<init>") {
                val all = (method.instructions.filter {
                    it.opcode == Opcodes.PUTFIELD && it is FieldInsnNode && it.desc == "Ljava/lang/String;"
                }).map { loadedClass.classNode.getFieldByName((it as FieldInsnNode).name) }
                MapKeyBinding.mapDescription.mapped = all[0]
                MapKeyBinding.mapCategory.mapped = all[1]
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapKeyBinding.mapPressTime.isMapped()
    }
}