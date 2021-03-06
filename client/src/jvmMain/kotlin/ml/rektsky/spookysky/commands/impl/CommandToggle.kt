package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.WebGui
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandToggle: Command("toggle", "Toggle a module", "toggle <Module Name>") {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        if (args.size != 1) {
            return false
        }
        val module = ModulesManager.getRegisteredModules()
            .firstOrNull { it.name.equals(args[0], true)}
        if (module == null) {
            sender.sendMessage("Module not found!", ChatColor.RED)
            return true
        }
        module.toggled = !module.toggled
        WebGui.broadcastPacket(PacketCommonUpdateModules().apply { modules = ArrayList(listOf(module.copy())) })
        if (module.toggled) {
            sender.sendMessage("${module.name} has been enabled!", ChatColor.GREEN)
        } else {
            sender.sendMessage("${module.name} has been disabled!", ChatColor.RED)
        }
        return true
    }

    override fun getAutoCompleteResult(sender: WebGuiInstance, args: Array<String>): Array<String> {
        if (args.size <= 1) {
            return ModulesManager.getRegisteredModules().map { it.name }.toTypedArray()
        }
        return arrayOf()
    }
}