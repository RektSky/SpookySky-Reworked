package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.WebGuiInstance
import kotlin.concurrent.withLock

class CommandProcessors: Command(
    "processors",
    "List all class processors and their status",
    "processor",
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        for (processor in ProcessorManager.processors) {
            sender.sendMessage(processor.javaClass.simpleName + " - ")
            sender.sendMessage("    Status: ${processor.thread.state.name}")
            sender.sendMessage("    Dependencies:")
            for (dependency in processor.dependencies) {
                sender.sendMessage("      - ${dependency.name}", if (dependency.isMapped()) ChatColor.GREEN else ChatColor.RED)
            }
            sender.sendMessage("    Job Done: ${processor.isJobDone()}", if (processor.isJobDone()) ChatColor.GREEN else ChatColor.RED)
            sender.sendMessage("")
        }

        sender.sendMessage("")
        sender.sendMessage("Instrumentation Instance: ${Client.instrumentation}")
        sender.sendMessage("")
        for (processor in ProcessorManager.processors) {
            Processor.threadPool.submit {
                processor.lock.withLock {
                    processor.condition.signal()
                }
            }
        }
        return true
    }


}