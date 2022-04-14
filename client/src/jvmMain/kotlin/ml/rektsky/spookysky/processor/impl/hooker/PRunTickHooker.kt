package ml.rektsky.spookysky.processor.impl.hooker

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.events.impl.game.ClientTickEvent
import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.getBytecodeName
import org.objectweb.asm.tree.TypeInsnNode


class MRunTickHooker: Processor() {

    init {
        dependsOn(MapMinecraft)
        dependsOn(MapMinecraft.mapRunTick)
    }

    var done = false

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        Client.debug("Processing RunTickHooker..")
        val methodNode = MapMinecraft.mapRunTick.mapped!!
        val classNode = MapMinecraft.mapped!!.classNode

        methodNode.instructions.insert(Event.generateEventCallingInstructions(ClientTickEvent::class.java))

        Client.debug("Successfully hooked TickEvent!")

        requestRedefineClass(classNode)
        done = true
    }

    override fun jobDone(): Boolean {
        return done
    }
}