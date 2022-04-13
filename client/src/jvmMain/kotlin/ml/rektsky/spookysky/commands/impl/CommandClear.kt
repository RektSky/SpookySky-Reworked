package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.packets.impl.server.PacketServerClearConsole
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandClear: Command(
    "clear",
    "Clear the console/terminal.",
    "clear",
    "cl"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        sender.send(PacketServerClearConsole())
        return true
    }

    override fun getAutoCompleteResult(sender: WebGuiInstance, args: Array<String>): Array<String> {
        return arrayOf()
    }
}