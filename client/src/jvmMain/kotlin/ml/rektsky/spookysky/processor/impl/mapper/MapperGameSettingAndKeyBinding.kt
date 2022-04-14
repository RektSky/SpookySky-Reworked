package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.mappings.settings.MapGameSettings
import ml.rektsky.spookysky.mapping.mappings.settings.MapKeyBinding
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.MappingUtils
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode

class MapperGameSettingAndKeyBinding: Processor() {

    init {

    }

    override fun process0(loadedClass: LoadedClass) {
        if (MappingUtils.hasString("key.togglePerspective", loadedClass.classNode)) {
            MapGameSettings.mapped = loadedClass
            for (method in loadedClass.classNode.methods) {
                if (method.name != "<init>") continue
                val instructions = method.instructions
                for (i in 0 until instructions.size()) {
                    val insnNode = instructions[i]
                    if (insnNode is LdcInsnNode) {
                        val firstOrNull = MapGameSettings.children.firstOrNull { it.name == insnNode.cst }
                        if (firstOrNull != null) {
                            val fieldMapping = firstOrNull as FieldMapping
                            fieldMapping.mapped = loadedClass.classNode.fields.first {
                                it.name == (instructions[i + 4] as FieldInsnNode).name
                            }
                        }
                    }
                }
            }
            val fieldMapping = MapGameSettings.children.first { it.isMapped() && it is FieldMapping && it.name.startsWith("key.") } as FieldMapping
            scheduleClassLoadAction(fieldMapping.mapped!!.desc.substring(1).let { it.substring(0, it.length-1) }) {
                MapKeyBinding.mapped = it
            }
        }

    }

    override fun jobDone(): Boolean {
        return MapGameSettings.isMapped() && MapKeyBinding.isMapped()
    }
}