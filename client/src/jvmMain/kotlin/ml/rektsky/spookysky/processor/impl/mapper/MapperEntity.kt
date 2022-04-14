package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.world.entity.MapEntity
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.MappingUtils
import ml.rektsky.spookysky.utils.getFieldByName
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.LdcInsnNode

class MapperEntity: Processor() {

    init {
        dependsOn(MapEntity)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapEntity.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val entityClazz = MapEntity
        val first = loadedClass.classNode.methods.filter { MappingUtils.hasString("Saving entity NBT", it) }.first()
        val instructions = first.instructions
        for (element in instructions.withIndex()) {
            val instruction = element.value
            val index = element.index
            if (instruction is LdcInsnNode && instruction.cst == "Pos") {
                val result = instructions.withIndex().filter { it.index > index }
                    .filter { it.value is FieldInsnNode && it.value.opcode == Opcodes.GETFIELD }
                MapEntity.mapPosX.mapped = loadedClass.classNode.getFieldByName((result[0].value as FieldInsnNode).name)
                MapEntity.mapPosY.mapped = loadedClass.classNode.getFieldByName((result[1].value as FieldInsnNode).name)
                MapEntity.mapPosZ.mapped = loadedClass.classNode.getFieldByName((result[2].value as FieldInsnNode).name)
                MapEntity.mapMotionX.mapped = loadedClass.classNode.getFieldByName((result[3].value as FieldInsnNode).name)
                MapEntity.mapMotionY.mapped = loadedClass.classNode.getFieldByName((result[4].value as FieldInsnNode).name)
                MapEntity.mapMotionZ.mapped = loadedClass.classNode.getFieldByName((result[5].value as FieldInsnNode).name)
                MapEntity.mapRotationYaw.mapped = loadedClass.classNode.getFieldByName((result[6].value as FieldInsnNode).name)
                MapEntity.mapRotationPitch.mapped = loadedClass.classNode.getFieldByName((result[7].value as FieldInsnNode).name)
                MapEntity.mapFallDistance.mapped = loadedClass.classNode.getFieldByName((result[8].value as FieldInsnNode).name)
                MapEntity.mapOnGround.mapped = loadedClass.classNode.getFieldByName((result[10].value as FieldInsnNode).name)
                MapEntity.mapInvulnerable.mapped = loadedClass.classNode.getFieldByName((result[12].value as FieldInsnNode).name)
            }
            if (instruction is LdcInsnNode && instruction.cst == "Silent") {
                val result = instructions.withIndex().filter { it.index > index }
                    .filter { it.value.opcode == Opcodes.IFNULL }
                MapEntity.mapRidingEntity.mapped = loadedClass.classNode.getFieldByName((instructions[result[0].index - 1] as FieldInsnNode).name)
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapEntity.mapPosX.isMapped()
    }
}