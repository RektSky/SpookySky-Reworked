package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.Client.debug
import ml.rektsky.spookysky.utils.ASMUtils
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.security.ProtectionDomain
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


object ProcessorManager {

    val processors = ArrayList<Processor>()

    private val classesLock = ReentrantLock()
    private val classes = HashMap<String, LoadedClass>()
    var progress = 0
    val executor = Executors.newFixedThreadPool(500) as ThreadPoolExecutor

    fun getClasses(): HashMap<String, LoadedClass> {
        classesLock.withLock {
            val hashMap = HashMap<String, LoadedClass>(classes)
            return hashMap
        }
    }
    init {
        val transformer: (loader: ClassLoader, className: String, classBeingRedefined: Class<*>, protectionDomain: ProtectionDomain, classfileBuffer: ByteArray) -> ByteArray =
            transformer@{ loader, className, classBeingRedefined, protectionDomain, classfileBuffer ->
                val node = ASMUtils.decompile(classfileBuffer)
                classes[className] = LoadedClass(classBeingRedefined, node)
                for (processor in processors) {
                    if (!processor.isJobDone()) {
                        processor.process(node)
                    }
                }
                return@transformer classfileBuffer
            }
        Client.instrumentation.addTransformer(transformer, true)
        val allLoadedClasses = Client.instrumentation.allLoadedClasses
        debug("Have to transform ${allLoadedClasses.size} classes")
        for (allLoadedClass in allLoadedClasses) {
            try {
                Client.instrumentation.retransformClasses(allLoadedClass)
            } catch (ignored: Throwable) {}
        }
    }

}

data class LoadedClass(
    val reflectionClass: Class<*>,
    val classNode: ClassNode
)