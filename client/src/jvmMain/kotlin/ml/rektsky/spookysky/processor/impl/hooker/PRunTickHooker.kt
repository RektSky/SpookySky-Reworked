package ml.rektsky.spookysky.processor.impl.hooker

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.events.impl.game.ClientTickEvent
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor


class MRunTickHooker: Processor() {

    init {
        dependsOn(Minecraft)
        dependsOn(Minecraft.mapRunTick)
    }

    var done = false

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == Minecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        Client.debug("Processing RunTickHooker..")
        val methodNode = Minecraft.mapRunTick.mapped!!
        val classNode = Minecraft.mapped!!.classNode

        methodNode.instructions.insert(Event.generateEventCallingInstructions(ClientTickEvent::class.java))

        Client.debug("Successfully hooked TickEvent!")

        requestRedefineClass(classNode)
        done = true
    }

    override fun jobDone(): Boolean {
        return done
    }
}