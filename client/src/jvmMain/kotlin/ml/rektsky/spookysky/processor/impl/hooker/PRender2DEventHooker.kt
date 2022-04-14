package ml.rektsky.spookysky.processor.impl.hooker

import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.events.impl.game.Render2DEvent
import ml.rektsky.spookysky.mapping.mappings.gui.MapGuiIngame
import ml.rektsky.spookysky.mapping.mappings.rendering.MapScaledResolution
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.MethodInsnNode

class PRender2DEventHooker: Processor() {

    init {
        dependsOn(MapGuiIngame)
        dependsOn(MapGuiIngame.mapRenderGameOverlay)
        dependsOn(MapScaledResolution)
    }

    var done = false

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass.classNode == MapGuiIngame.mapped?.classNode
    }

    override fun process0(loadedClass: LoadedClass) {
        var found = 0
        val out = InsnList()
        for (instruction in MapGuiIngame.mapRenderGameOverlay.mapped!!.instructions) {
            out.add(instruction)
            if (instruction is MethodInsnNode) {
                if (instruction.desc == "(L${MapScaledResolution.mapped!!.classNode.name};F)V") {
                    found++
                }
                if (found == 2) {
                    done = true
                    out.add(Event.generateEventCallingInstructions(
                        Render2DEvent::class.java
                    ))
                }
            }
        }

        MapGuiIngame.mapRenderGameOverlay.mapped!!.instructions = out

        requestRedefineClass(MapGuiIngame.mapped!!.classNode)
    }

    override fun jobDone(): Boolean {
        return done
    }
}