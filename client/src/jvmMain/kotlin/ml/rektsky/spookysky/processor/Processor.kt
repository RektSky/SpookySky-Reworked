package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.Mapping
import ml.rektsky.spookysky.utils.ASMUtils.compile
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.HttpServerThread
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.util.CheckClassAdapter
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.io.PrintWriter
import java.lang.instrument.ClassDefinition
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.concurrent.withLock

class CustomClassDef(
    val loadedClass: LoadedClass
) {

    fun getReflectiveClass(): Class<*>? {
        return try {
            Class.forName(loadedClass.classNode.name.replace("/", "."), false, loadedClass.classLoader)
        } catch (ignored: Throwable) {
            null
        }
    }

    fun execute(): Boolean {
        try {
            val clazz = getReflectiveClass()
            if (clazz == null) return false
            var compileResult: ByteArray
            compileResult = this.loadedClass.classNode.compile()
            if (false) {
                try {
                    var illegal = false
                    CheckClassAdapter.verify(ClassReader(compileResult), false, PrintWriter(
                        object: OutputStream() {
                            override fun write(b: Int) {
                                illegal = true
                            }
                        }))
                    if (illegal) {
                        val out = ByteArrayOutputStream()
                        CheckClassAdapter.verify(ClassReader(compileResult), false, PrintWriter(out))
                        val message = out.toString("utf-8")
                        Client.error(IllegalStateException("Failed to verify class: Invalid class detected"))
                        Client.debug("Failed to verify class! Forcing it to redefine will most likely crash the JVM!", ChatColor.RED)
                        for (s in message.split("\n")) {
                            Client.debug(s, ChatColor.RED)
                        }
                        Client.debug("")
                        Client.debug("Class being redefined: ${clazz.name}", ChatColor.RED)
                        Client.debug("")
                        Client.debug("If you want to download the invalid class, please download it in the link below", ChatColor.RED)
                        val url = "/invalid-bytecode-" + UUID.randomUUID() + "/" + clazz.name + ".class"
                        HttpServerThread.server.createContext(url) {
                            it.sendResponseHeaders(200, compileResult.size.toLong())
                            it.responseBody.write(compileResult)
                            it.responseBody.close()
                        }
                        Client.debug("%BASE_URL%$url", ChatColor.RED)
                        return false
                    }
                } catch (e: Throwable) {
                    throw e
                }
            }

            Client.instrumentation.redefineClasses(ClassDefinition(clazz, compileResult))
            Client.debug(" - Redefined ${clazz.simpleName}")
            return true
        } catch (ignored: Throwable) {
            return false
        }
    }
}

abstract class Processor {

    companion object NotifyingThreadPool {
        val threadPool = Executors.newFixedThreadPool(2)
    }
    internal val scheduledRedefine = ArrayList<CustomClassDef>()

    internal val lock = ReentrantLock()
    internal val condition = lock.newCondition()
    internal var dependencies = ArrayList<Mapping<*>>()

    internal val scheduledClassLoadActions = HashMap<String, (clazz: LoadedClass) -> Unit>()

    private val processQueue = ArrayList<LoadedClass>()

    private var done = false

    private var initializationTime = System.currentTimeMillis()
    // Avoid lunar hash check

    val thread = Thread {
        while (true) {
            var failed = false
            for (dependency in dependencies) {
                if (!dependency.isMapped()) {
                    failed = true
                    lock.withLock {
                        condition.await()
                    }
                }
            }
            if (!failed) break
        }
        Client.debug(" - Every dependency of " + javaClass.simpleName + " has been resolved! Processing...")
        while (!isJobDone()) {
            while (scheduledRedefine.isNotEmpty()) {
                if (System.currentTimeMillis() - initializationTime < 10000) break
                val reDef = scheduledRedefine.first()
                if (reDef.execute()) {
                    Client.debug("${reDef.loadedClass.classNode.name} has been marked as done!")
                    scheduledRedefine.removeFirst()
                }
            }
            while (processQueue.isNotEmpty()) {
                try {
                    if (processQueue.first() == null) {
                        processQueue.removeFirst()
                        continue
                    }
                    if (shouldProcess(processQueue.first())) {
                        process0(processQueue.removeFirst())
                    } else {
                        processQueue.removeFirst()
                    }
                } catch (e: Exception) {
                    Client.error(e)
                }
            }
            Thread.sleep(100)
        }
        while (scheduledRedefine.isNotEmpty()) {
            if (System.currentTimeMillis() - initializationTime < 10000) {
                Thread.sleep(10000 - System.currentTimeMillis() + initializationTime)
            }
            val reDef = scheduledRedefine.first()
            if (reDef.execute()) {
                Client.debug("${reDef.loadedClass.classNode.name} has been marked as done!")
                scheduledRedefine.removeFirst()
            }
        }
    }

    protected open fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return true
    }
    protected abstract fun process0(loadedClass: LoadedClass)
    protected abstract fun jobDone(): Boolean

    protected fun requestReprocess(loadedClass: LoadedClass) {
        processQueue.add(loadedClass)
    }

    protected fun scheduleClassLoadAction(name: String, action: (loadedClass: LoadedClass) -> Unit) {
        val name = name.replace(".", "/")
        if (name in scheduledClassLoadActions.keys) {
            throw IllegalStateException("Class load action has been scheduled already! Class name: ${name}")
        }
        val loadedClass = ProcessorManager.getClasses()[name]
        if (loadedClass != null) {
            action(loadedClass)
            return
        }
        scheduledClassLoadActions[name] = action
    }

    protected fun requestRedefineClass(classNode: ClassNode) {
        val loadedClass = ProcessorManager.getClasses()[classNode.name]!!
        if (ProcessorManager.transforming?.classNode != classNode) {
            if (!CustomClassDef(loadedClass).execute()) {
                Client.debug("Class ${classNode.name} is not loaded yet! Adding to queue... (transforming: ${ProcessorManager.transforming?.classNode?.name}")
                scheduledRedefine.add(CustomClassDef(loadedClass))
            } else {
//                println(Thread.currentThread().stackTrace.joinToString("\n"))
            }
        }
    }

    // Lazy loads the `jobDone()`
    fun isJobDone(): Boolean {
        if (done) return true
        if (jobDone()) {
            done = true
            return true
        }
        return false
    }

    fun dependsOn(mapping: Mapping<*>) {
        dependencies.add(mapping)
    }

    fun process(node: LoadedClass) {
        if (node.classNode.name.startsWith("ml/rektsky/spookysky")) return
        for (dependency in dependencies) {
            if (!dependency.isMapped()) {
                processQueue.add(node)
                return
            }
        }
        if (shouldProcess(node)) {
            process0(node)
        }
    }

    internal fun start() {
        thread.start()
    }

}
