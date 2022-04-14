package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.mapping.mappings.ClientBrandRetriever
import ml.rektsky.spookysky.mapping.mappings.MapClientBrandRetriever
import ml.rektsky.spookysky.webgui.WebGuiInstance

class CommandVersion: Command(
    "version",
    "Show the version/client brand of the version you are using",
    "version"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        sender.sendMessage("Current Client Brand: ${ClientBrandRetriever.getClientModName()}")
        return true
    }
}