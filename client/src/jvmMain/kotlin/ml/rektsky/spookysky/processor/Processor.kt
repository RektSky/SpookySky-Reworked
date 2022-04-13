package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.Mapping
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

abstract class Processor {

    companion object NotifyingThreadPool {
        val threadPool = Executors.newFixedThreadPool(10)
    }

    internal val lock = ReentrantLock()
    internal val condition = lock.newCondition()
    internal var dependencies = ArrayList<Mapping<*>>()

    internal val scheduledClassLoadActions = HashMap<String, (clazz: LoadedClass) -> Unit>()

    private val processQueue = ArrayList<LoadedClass>()

    private var done = false

    val thread = Thread {
        while (true) {
            var failed = false
            for (dependency in dependencies) {
                if (!dependency.isMapped()) {
                    failed = true
                    threadPool.submit {
                        lock.withLock {
                            condition.await()
                        }
                    }
                }
            }
            if (!failed) break
        }
        Client.debug(" - Every dependency of " + javaClass.simpleName + " has been resolved! Processing...")
        while (!isJobDone()) {
            while (processQueue.isNotEmpty()) {
                try {
                    process0(processQueue.removeFirst())
                } catch (e: Exception) {
                    Client.error(e)
                }
            }
            Thread.sleep(100)
        }
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
        processQueue.add(node)
    }

    internal fun start() {
        thread.start()
    }

}
