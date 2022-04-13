package ml.rektsky.spookysky.processor

import ml.rektsky.spookysky.mapping.ClassMapping
import org.objectweb.asm.tree.ClassNode
import java.util.concurrent.locks.ReentrantLock

abstract class Processor {

    internal val lock = ReentrantLock()
    internal val condition = lock.newCondition()
    internal var dependencies = ArrayList<ClassMapping>()

    private val processQueue = ArrayDeque<ClassNode>()

    private var done = false

    protected abstract fun process0(node: ClassNode): Boolean
    protected abstract fun jobDone(): Boolean

    // Lazy loads the `jobDone()`
    fun isJobDone(): Boolean {
        if (done) return true
        if (jobDone()) {
            done = true
            return true
        }
        return false
    }

    fun dependsOn(mapping: ClassMapping) {
        dependencies.add(mapping)
    }

    fun process(node: ClassNode) {
        processQueue.add(node)
    }

    internal fun start() {
        Thread {
            while (!dependencies.all { it.isMapped() }) {
                condition.await()
            }
            while (!isJobDone()) {
                while (processQueue.isEmpty()) {
                    process0(processQueue.removeFirst())
                }
                Thread.sleep(100)
            }
        }.start()
    }

}
