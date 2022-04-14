package ml.rektsky.spookysky.processor.impl.hooker

import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.events.impl.game.MouseUpdateEvent
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode

const val magicString = "mouse"

class PMouseUpdateHooker: Processor() {

    private var hooked = false

    init {
        dependsOn(Minecraft)
        dependsOn(Minecraft.mapRunTick)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == Minecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        val runTick = Minecraft.mapRunTick.mapped!!
        var newList: InsnList = InsnList()
        for (instruction in runTick.instructions) {
            newList.add(instruction)
            if (instruction is LdcInsnNode) {
                if (instruction.cst == magicString) {
                    newList.add(Event.generateEventCallingInstructions(MouseUpdateEvent::class.java))
                }
            }
        }
        runTick.instructions = newList
        hooked = true
        requestRedefineClass(Minecraft.mapped!!.classNode)
    }

    override fun jobDone(): Boolean {
        return hooked
    }
}