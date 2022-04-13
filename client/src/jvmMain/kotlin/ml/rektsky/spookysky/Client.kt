package ml.rektsky.spookysky

import ml.rektsky.spookysky.commands.CommandsManager
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.processor.ProcessorManager
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
            debug("Loading SpookySky...")
            debug(" - Debug Run Detected!")
            init()
        } else {
            try {
                val socket = Socket("127.0.0.1", 6931)
                debug("Injection Information:")
                for (stackTraceElement in Thread.currentThread().stackTrace) {
                    debug(stackTraceElement.toString())
                }
                debug("Loading SpookySky...")
                CustomJvmSelfAttach.init(File(System.getProperty("java.io.tmpdir")))
                instrumentation = CustomJvmSelfAttach.getInstrumentation()
                debug("Successfully injected into itself! Initializing client...")
                init()
            } catch (e: Exception) {
                if (debug) {
                    e.printStackTrace()
                }
            }
        }
    }

    internal fun init() {
        debug("Initializing Events Manager..")
        EventsManager
        debug("Initializing Modules Manager..")
        ModulesManager
        debug("Initializing Web GUI..")
        WebGui
        debug("Initializing Commands Manager...")
        CommandsManager
        if (!webGuiOnly) {
            debug("Initializing Processor Manager...")
            ProcessorManager
        }

    }


    fun debug(message: String) {
        if (debug) println("[SpookySky Debug] $message")
    }
}


fun main() {
    webGuiOnly = true
    Client
}