package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.commands.CommandsManager
import ml.rektsky.spookysky.utils.times
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandHelp: Command(
    "help",
    "List all commands, and shows their description/usage.",
    "help",
    "cmd"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        sender.sendMessage("========== Commands ==========")
        for (command in CommandsManager.commands) {
            sender.sendMessage(command.usage.let { it + " "*(25-it.length) } + " - ${command.description}")
        }
        return true
    }


}