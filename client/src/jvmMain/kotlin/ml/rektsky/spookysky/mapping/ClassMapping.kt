package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.processor.ProcessorManager
import kotlin.concurrent.withLock

abstract class ClassMapping {

    var mapped: Class<*>? = null
        protected set(value) {
            field = value
            for (processor in ProcessorManager.processors) {
                if (processor.lock.isLocked) {
                    processor.lock.withLock {
                        processor.condition.signalAll()
                    }
                }
            }
        }




    fun isMapped(): Boolean {
        return mapped != null
    }

}