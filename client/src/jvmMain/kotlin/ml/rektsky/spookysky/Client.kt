package ml.rektsky.spookysky

import io.github.kasukusakura.jsa.JvmSelfAttach
import java.io.File

object Client {

    init {
        JvmSelfAttach.init(File(System.getProperty("java.io.tmpdir")))
        val instrumentation = JvmSelfAttach.getInstrumentation()
    }

}