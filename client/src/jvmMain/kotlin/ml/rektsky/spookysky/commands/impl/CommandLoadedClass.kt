package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandLoadedClass: Command(
    "classes",
    "[DEBUG] Show all classes loaded by ProcessorManager",
    "classes [Regex...]"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        var matcher = {s: String ->
            true
        }
        if (args.isNotEmpty()) {
            val regexes: Array<Regex> = args.map { Regex(it) }.toTypedArray()
            matcher = lambda@{
                for (regex in regexes) {
                    if (!regex.matches(it)) return@lambda false
                }
                true
            }
        }

        for (entry in ProcessorManager.getClasses()) {
            if (matcher(entry.value.classNode.name)) {
                sender.sendMessage(" - " + entry.value.classNode.name)
            }
        }
        sender.sendMessage("========= Statistics =========")
        sender.sendMessage("Expected: ${Client.instrumentation.allLoadedClasses.size}")
        sender.sendMessage("Real    : ${ProcessorManager.getClasses().size}")
        return true
    }
}