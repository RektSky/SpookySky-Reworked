package ml.rektsky.spookysky

import ml.rektsky.spookysky.utils.CustomJvmSelfAttach
import java.io.File

object Client {

    init {
        try {
            CustomJvmSelfAttach.init(File(System.getProperty("java.io.tmpdir")))
            val instrumentation = CustomJvmSelfAttach.getInstrumentation()
            println("Successfully injected! Instrumentation: $instrumentation")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}