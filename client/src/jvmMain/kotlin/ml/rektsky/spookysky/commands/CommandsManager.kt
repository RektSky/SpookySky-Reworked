package ml.rektsky.spookysky.commands

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.WebGuiPacketEvent
import ml.rektsky.spookysky.packets.impl.client.PacketClientExecuteCommand
import ml.rektsky.spookysky.packets.impl.client.PacketClientRequestAutoComplete
import ml.rektsky.spookysky.packets.impl.server.PacketServerAutoCompleteResponse
import ml.rektsky.spookysky.packets.impl.server.PacketServerConsoleMessage
import ml.rektsky.spookysky.utils.ClassUtils
import java.awt.Color

object CommandsManager {

    val commands = ArrayList<Command>()

    init {
        EventsManager.register(this)
        for (clazz in ClassUtils.resolvePackage(javaClass.`package`.name, Command::class.java)) {
            val command = clazz.newInstance()
            commands.add(command)
            Client.debug("[Commands Manager] Registered command: ${command.name}")
        }
    }


    @EventHandler
    fun onPacket(event: WebGuiPacketEvent) {
        val packet = event.packet
        if (packet is PacketClientExecuteCommand) {
            event.sender.send(PacketServerConsoleMessage("Executed command: ${packet.command}", 0x40A9F6))
        }
        if (packet is PacketClientRequestAutoComplete) {
            if (packet.command.isNotEmpty()) {
                event.sender.send(PacketServerAutoCompleteResponse(
                    "Test ",
                    "Fly ",
                    "Speed ",
                    "KillAura "
                ))
            }
        }
    }

}