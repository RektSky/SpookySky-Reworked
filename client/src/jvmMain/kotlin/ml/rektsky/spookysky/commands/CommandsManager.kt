package ml.rektsky.spookysky.commands

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.WebGuiPacketEvent
import ml.rektsky.spookysky.packets.impl.client.PacketClientExecuteCommand
import ml.rektsky.spookysky.packets.impl.client.PacketClientRequestAutoComplete
import ml.rektsky.spookysky.packets.impl.server.PacketServerAutoCompleteResponse
import ml.rektsky.spookysky.utils.ClassUtils
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object CommandsManager {

    val commands = ArrayList<Command>()

    private val lock = ReentrantLock()
    private val notifier = lock.newCondition()

    val scheduledPacketProcessing = ArrayList<WebGuiPacketEvent>()

    init {
        EventsManager.register(this)
        for (clazz in ClassUtils.resolvePackage(javaClass.`package`.name, Command::class.java)) {
            val command = clazz.newInstance()
            commands.add(command)
            Client.debug("[Commands Manager] Registered command: ${command.name}")
        }
        Thread {
            while (true) {
                lock.withLock {
                    notifier.await()
                    for (webGuiPacketEvent in scheduledPacketProcessing) {
                        processPacket(webGuiPacketEvent)
                    }
                    scheduledPacketProcessing.clear()
                }
            }
        }.start()
    }

    @EventHandler
    fun onPacket(event: WebGuiPacketEvent) {
        scheduledPacketProcessing.add(event)
        lock.withLock {
            notifier.signal()
        }
    }

    fun processPacket(event: WebGuiPacketEvent) {
        val packet = event.packet
        val sender = event.sender
        if (packet is PacketClientExecuteCommand) {
            val command = packet.command
            val split = command.split(" ")
            if (split.size > 0) {
                val commandName = split[0]
                val foundCommand = commands.firstOrNull { it.name == commandName || it.aliases.contains(commandName) }
                if (foundCommand == null) {
                    sender.sendMessage("Unknown command! Type \"help\" for commands list.", 0xFF5351)
                    return
                }
                println(split.joinToString(" "))
                foundCommand.executeCommand(sender, split.subList(1, split.size).toTypedArray())
            } else {
                sender.sendMessage("You just sent an empty command!", 0xFF5351)
            }
        }
        if (packet is PacketClientRequestAutoComplete) {
            val command = packet.command
            if (command.isNotEmpty()) {
                val split = command.split(" ")
                if (split.size == 1) {
                    sender.send(PacketServerAutoCompleteResponse(*commands.map { it.name }.filter { it.startsWith(split[0]) }.toTypedArray()))
                    return
                }
                val commandName = split[0]
                val foundCommand = commands.firstOrNull { it.name == commandName } ?: return
                sender.send(PacketServerAutoCompleteResponse(*foundCommand.getAutoCompleteResult(sender, split.subList(1, split.size).toTypedArray())))
            } else {
                sender.send(PacketServerAutoCompleteResponse(*commands.map { it.name }.toTypedArray()))
            }
        }
    }

}

