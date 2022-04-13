package ml.rektsky.spookysky.commands

import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.WebGuiInstance

abstract class Command(
    val name: String,
    val description: String,
    val usage: String,
    vararg val aliases: String = arrayOf()
) {

    fun executeCommand(sender: WebGuiInstance, args: Array<String>) {
        try {
            if (!onCommand(sender, args)) {
                sender.sendMessage("Incorrect usage! Usage: $usage", 0xFF5351)
            }
        } catch (e: Throwable) {
            sender.sendMessage("Something went wrong while processing the command!", ChatColor.RED)
            sender.send(e)
        }
    }

    protected abstract fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean
    open fun getAutoCompleteResult(sender: WebGuiInstance, args: Array<String>): Array<String> {
        return arrayOf()
    }


}