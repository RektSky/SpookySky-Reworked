package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.Mapping
import ml.rektsky.spookysky.utils.ASMUtils
import ml.rektsky.spookysky.utils.ClassUtils
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.lang.reflect.Modifier
import java.security.ProtectionDomain
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


object ProcessorManager {

    val processors = ArrayList<Processor>()
    val mappings = ArrayList<Mapping<*>>()

    private val classesLock = ReentrantLock()
    private val classes = HashMap<String, LoadedClass>()

    fun getClasses(): HashMap<String, LoadedClass> {
        classesLock.withLock {
            val hashMap = HashMap<String, LoadedClass>(classes)
            return hashMap
        }
    }
    init {
        for (clazz in ClassUtils.resolvePackage(javaClass.`package`.name, Processor::class.java)) {
            Client.debug("[ProcessorManager] Registered processor: ${clazz.simpleName}")
            val element = clazz.newInstance()
            processors.add(element)
            element.start()
        }


        Client.instrumentation.addTransformer(CustomTransformer(), true)



        val allLoadedClasses = Client.instrumentation.allLoadedClasses
            Client.debug("Have to transform ${allLoadedClasses.size} classes")
        for (allLoadedClass in allLoadedClasses) {
            if (
                allLoadedClass.name.startsWith("java") ||
                allLoadedClass.name.startsWith("jdk") ||
                allLoadedClass.name.startsWith("sun")
            ) {
                continue
            }
            try {
                Client.instrumentation.retransformClasses(allLoadedClass)
            } catch (ignored: Throwable) {}
        }


        Client.debug("Registering mappings...")
        ClassUtils.resolvePackage(Client.javaClass.`package`.name, Mapping::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }.forEach {
                mappings.add(it.getField("INSTANCE").get(null) as Mapping<*>)
                Client.debug("Registered mapping: ${(it.getField("INSTANCE").get(null) as Mapping<*>).name}")
            }
    }
    class CustomTransformer: ClassFileTransformer {
        override fun transform(
            loader: ClassLoader?,
            className: String?,
            classBeingRedefined: Class<*>?,
            protectionDomain: ProtectionDomain?,
            classfileBuffer: ByteArray?
        ): ByteArray {
            val node = ASMUtils.decompile(classfileBuffer!!)
            val loadedClass = LoadedClass(node)
            classesLock.withLock {
                classes[className!!] = loadedClass
            }
            for (processor in processors) {
                if (!processor.isJobDone()) {
                    processor.process(loadedClass)
                }
                val function = processor.scheduledClassLoadActions[className]
                if (function != null) {
                    function(loadedClass)
                }
            }
            return classfileBuffer
        }

    }
}



class LoadedClass(
    val classNode: ClassNode
) {
    
    fun getReflectionClass(): Class<*> {
        return Class.forName(classNode.name.replace("/", "."))
    }
    
}