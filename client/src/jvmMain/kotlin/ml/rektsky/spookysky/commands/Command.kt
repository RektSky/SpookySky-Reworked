package ml.rektsky.spookysky.commands

import ml.rektsky.spookysky.webgui.WebGuiInstance

abstract class Command(
    val name: String,
    val description: String,
    val usage: String,
    val aliases: Array<String> = arrayOf()
) {

    fun executeCommand(sender: WebGuiInstance, args: Array<String>) {

    }

    protected abstract fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean
    abstract fun getAutoCompleteResult(sender: WebGuiInstance, args: Array<String>): Array<String>


}