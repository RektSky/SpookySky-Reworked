package ml.rektsky.spookysky

import ml.rektsky.spookysky.commands.CommandsManager
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.processor.ProcessorManager
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.utils.CustomJvmSelfAttach
import ml.rektsky.spookysky.webgui.WebGui
import java.io.File
import java.lang.instrument.Instrumentation
import java.net.Socket
var webGuiOnly = false

object Client {

    const val safe = true
    const val debug = true
    const val disableMinecraftLog = true

    val oldStream = System.out

    lateinit var instrumentation: Instrumentation
        private set

    init {
        if (webGuiOnly) {
            addConsoleMessage("Loading SpookySky...")
            addConsoleMessage(" - Debug Run Detected!")
            init()
        } else {
            try {
                val socket = Socket("127.0.0.1", 6931)
                addConsoleMessage("Injection Information:")
                for (stackTraceElement in Thread.currentThread().stackTrace) {
                    addConsoleMessage(stackTraceElement.toString())
                }
                addConsoleMessage("Loading SpookySky...")
                CustomJvmSelfAttach.init(File(System.getProperty("java.io.tmpdir")))
                instrumentation = CustomJvmSelfAttach.getInstrumentation()
                addConsoleMessage("Successfully injected into itself! Initializing client...")
                init()
            } catch (e: Exception) {
                if (debug) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal fun init() {
        addConsoleMessage("Initializing Events Manager..")
        EventsManager
        addConsoleMessage("Initializing Modules Manager..")
        ModulesManager
        addConsoleMessage("Initializing Web GUI..")
        WebGui
        addConsoleMessage("Initializing Commands Manager...")
        CommandsManager
        if (!webGuiOnly) {
            addConsoleMessage("Initializing Processor Manager...")
            ProcessorManager
        }

    }


    fun addConsoleMessage(message: String) {
        if (debug) println("[SpookySky Debug] $message")
    }

    fun debug(message: String, color: Int = ChatColor.GRAY) {
        WebGui.message("[DEBUG] " + message, color, true)
    }

    fun error(e: Throwable) {
        for (connectedClient in WebGui.getConnectedClients()) {
            connectedClient.send(e)
        }
    }

}


fun main() {
    webGuiOnly = true
    Client
}