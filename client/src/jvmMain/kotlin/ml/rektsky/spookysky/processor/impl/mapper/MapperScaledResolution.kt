package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.mapping.mappings.gui.MapGuiIngame
import ml.rektsky.spookysky.mapping.mappings.rendering.MapScaledResolution
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class MapperScaledResolution: Processor() {

    init {
        dependsOn(MapMinecraft)
        dependsOn(MapGuiIngame)
        dependsOn(MapGuiIngame.mapRenderGameOverlay)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapGuiIngame.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val instructions = MapGuiIngame.mapRenderGameOverlay.mapped!!.instructions
        var index = -1
        for (instruction in instructions) {
            index++
            if (instruction is FieldInsnNode) {
                if (instruction.desc == "L${MapMinecraft.mapped!!.classNode.name};") {
                    var scaledResolutionClassName = (instructions.let { it[index + 1] } as MethodInsnNode).owner
                    scheduleClassLoadAction(scaledResolutionClassName) {
                        MapScaledResolution.mapped = it
                    }
                    return
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapScaledResolution.isMapped()
    }
}