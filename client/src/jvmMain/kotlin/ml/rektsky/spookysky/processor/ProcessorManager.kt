package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.utils.ASMUtils
import ml.rektsky.spookysky.utils.ClassUtils
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.security.ProtectionDomain
import java.util.concurrent.locks.ReentrantLock

object ProcessorManager {

    val processors = ArrayList<Processor>()

    private val classesLock = ReentrantLock()
    private val classes = HashMap<String, LoadedClass>()

    fun getClasses(): HashMap<String, LoadedClass> {
        classesLock.lock()
        val hashMap = HashMap<String, LoadedClass>(classes)
        classesLock.unlock()
        return hashMap
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
        Client.instrumentation.retransformClasses(*Client.instrumentation.allLoadedClasses)

        for (clazz in ClassUtils.resolvePackage(javaClass.`package`.name, Processor::class.java)) {
            processors.add(clazz.newInstance())
        }

        for (processor in processors) {
            processor.start()
        }
    }

}

data class LoadedClass(
    val reflectionClass: Class<*>,
    val classNode: ClassNode
)