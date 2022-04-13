package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandLoadedClass: Command(
    "classes",
    "Show all classes loaded by ProcessorManager",
    "classes"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {

        sender.sendMessage("========= Statistics =========")
        sender.sendMessage("Loaded by client : ${ProcessorManager.getClasses().size}")
        sender.sendMessage("Loaded by JVM    : ${Client.instrumentation.allLoadedClasses.size}")
        return true
    }
}