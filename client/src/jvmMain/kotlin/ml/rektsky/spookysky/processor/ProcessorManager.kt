package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.Mapping
import ml.rektsky.spookysky.utils.ASMUtils
import ml.rektsky.spookysky.utils.ASMUtils.compile
import ml.rektsky.spookysky.utils.ClassUtils
import org.objectweb.asm.tree.ClassNode
import java.lang.instrument.ClassFileTransformer
import java.lang.reflect.Modifier
import java.security.ProtectionDomain
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock



object ProcessorManager {

    val processors = ArrayList<Processor>()
    val mappings = ArrayList<ClassMapping>()


    var transforming: LoadedClass? = null
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
            if (Modifier.isAbstract(clazz.modifiers)) continue
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
        ClassUtils.resolvePackage(Client.javaClass.`package`.name, ClassMapping::class.java)
            .filter { !Modifier.isAbstract(it.modifiers) }.forEach {
                mappings.add(it.getField("INSTANCE").get(null) as ClassMapping)
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
            val loadedClass = LoadedClass(node, classfileBuffer, loader!!)
            classesLock.withLock {
                if (className in classes) {
                    classes[className!!] = loadedClass
                    return classfileBuffer
                }
                classes[className!!] = loadedClass
            }
            transforming = loadedClass
            for (processor in processors) {
                if (!processor.isJobDone()) {
                    processor.process(loadedClass)
                }
                val function = processor.scheduledClassLoadActions[className]
                if (function != null) {
                    function(loadedClass)
                }
            }
            transforming = null
            return loadedClass.classNode.compile()
        }

    }
}



data class LoadedClass(
    val classNode: ClassNode,
    val rawData: ByteArray,
    val classLoader: ClassLoader
) {
    
    fun getReflectionClass(): Class<*> {
        return Class.forName(classNode.name.replace("/", "."), false, classLoader)
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LoadedClass) return false
        return classNode.name == other.classNode.name
    }

    override fun hashCode(): Int {
        var result = classNode.hashCode()
        result = 31 * result + rawData.contentHashCode()
        return result
    }

}