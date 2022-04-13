package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.HttpServerThread
import ml.rektsky.spookysky.webgui.WebGuiInstance
import java.util.UUID

class CommandExportClass: Command(
    "exportclass",
    "Download a class (for debugging purposes)",
    "exportclass <remapped | original> <Remap name | original full class name>"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        if (args.size != 2) return false
        val mode = args[0]
        val className = args[1]

        var targetClass: String? = null
        if (mode == "remapped") {
            val mapping = ProcessorManager.mappings.firstOrNull { it.name == className }
            if (mapping == null) {
                sender.sendMessage("Mapping: ${className} is not found! Use \"mapstatus\" command for map lists.", ChatColor.RED)
                return true
            }
            if (!mapping.isMapped()) {
                sender.sendMessage("Mapping: ${mapping.name} " +
                        "has not been mapped yet!", ChatColor.RED)
                return true
            }
            targetClass = mapping.mapped?.classNode?.name
        } else if (mode == "original") {
            targetClass = className
        } else {
            return false
        }
        val loadedClass = ProcessorManager.getClasses()[targetClass]
        if (targetClass == null || loadedClass == null) {
            sender.sendMessage("Target class is either not found, not loaded, or not being able to be " +
                    "retransformed", ChatColor.RED)
            return true
        }
        val copy = loadedClass.copy()

        val url = "/" + UUID.randomUUID() + "-" + targetClass + ".class"
        HttpServerThread.server.createContext(url) {
            it.sendResponseHeaders(200, copy.rawData.size.toLong())
            it.responseBody.write(copy.rawData)
            it.responseBody.close()
        }
        sender.sendMessage("Exported! Please visit %BASE_URL%$url", ChatColor.GREEN)
        return true
    }

    override fun getAutoCompleteResult(sender: WebGuiInstance, args: Array<String>): Array<String> {
        if (args.size <= 1) {
            return arrayOf("remapped", "original")
        } else if (args[0] == "remapped") {
            return arrayOf(*ProcessorManager.mappings.map { it.name }.toTypedArray())
        }
        return arrayOf()
    }
}