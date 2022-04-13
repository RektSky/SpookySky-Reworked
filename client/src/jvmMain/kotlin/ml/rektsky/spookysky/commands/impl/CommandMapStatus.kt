package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.Mapping
import ml.rektsky.spookysky.mapping.MethodMapping
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.utils.ClassUtils
import ml.rektsky.spookysky.webgui.WebGuiInstance
import java.lang.reflect.Modifier

class CommandMapStatus: Command(
    "mapstatus",
    "Show the current status of auto mapper",
    "mapstatus [Query]",
    "ms"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        var query = if (args.isEmpty()) "" else args.joinToString(" ")

        for (mapping in ProcessorManager.mappings) {
            if (mapping is ClassMapping && query in mapping.name) {
                sender.sendMessage(mapping.name +
                        if(mapping.isMapped()) "  -  ${mapping.mapped!!.classNode.name}" else "  -  UNMAPPED",
                        if (mapping.isMapped()) ChatColor.GREEN else ChatColor.RED)
            }
            if (mapping is ClassMapping) {
                if (mapping.children.any {query in it.name}) {
                    sender.sendMessage(mapping.name +
                            if(mapping.isMapped()) "  -  ${mapping.mapped!!.classNode.name}" else "  -  UNMAPPED",
                        if (mapping.isMapped()) ChatColor.GREEN else ChatColor.RED)
                }
                for (child in mapping.children) {
                    if (child is FieldMapping && query in child.name) {
                        sender.sendMessage("    ${child.name}" + if (child.isMapped()) "  -  ${child.mapped!!.name}" else "  -  UNMAPPED",
                            if (child.isMapped()) ChatColor.GREEN else ChatColor.RED)
                    }
                    if (child is MethodMapping && query in child.name) {
                        sender.sendMessage("    ${child.name}" + if (child.isMapped()) "  -  ${child.mapped!!.name}" else "  -  UNMAPPED",
                            if (child.isMapped()) ChatColor.GREEN else ChatColor.RED)
                    }
                }
            }
        }
        return true
    }


}