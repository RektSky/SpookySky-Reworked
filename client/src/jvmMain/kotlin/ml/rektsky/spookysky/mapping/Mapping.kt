package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.processor.ProcessorManager
import kotlin.concurrent.withLock

abstract class Mapping<T>(val name: String) {

    var mapped: T? = null
        set(value) {
            if (value != null && field == null) {
                Client.debug("$name has been mapped")
            }
            field = value
            for (processor in ProcessorManager.processors) {
                if (processor.lock.isLocked) {
                    processor.lock.withLock {
                        processor.condition.signal()
                    }
                }
            }
        }

    fun isMapped(): Boolean {
        return mapped != null
    }

}