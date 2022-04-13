package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.webgui.WebGui
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandBroadcast: Command("broadcast", "Broadcast a message to every webclient", "broadcast <message>") {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        WebGui.message(args.joinToString(" "))
        return true
    }
}