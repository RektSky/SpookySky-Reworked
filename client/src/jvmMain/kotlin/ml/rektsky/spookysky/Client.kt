package ml.rektsky.spookysky

import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.utils.CustomJvmSelfAttach
import ml.rektsky.spookysky.webgui.WebGui
import java.io.File
import java.lang.instrument.Instrumentation
import java.net.Socket

object Client {

    const val debug = true

    lateinit var instrumentation: Instrumentation
        private set

    init {
        for (i in 1..500){
            println("GOT YOU")
        }
        try {
            val socket = Socket("127.0.0.1", 6931)
            CustomJvmSelfAttach.init(File(System.getProperty("java.io.tmpdir")))
            instrumentation = CustomJvmSelfAttach.getInstrumentation()
            init()
        } catch (e: Exception) {
            if (debug) {
                e.printStackTrace()
            }
        }
    }

    private fun init() {
        EventsManager
        ModulesManager
        WebGui
    }


    fun debug(message: String) = println(message)

}