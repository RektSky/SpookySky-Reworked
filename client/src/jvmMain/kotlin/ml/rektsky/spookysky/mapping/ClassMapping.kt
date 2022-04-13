package ml.rektsky.spookysky.mapping

import ml.rektsky.spookysky.processor.ProcessorManager

abstract class ClassMapping {

    var mapped: Class<*>? = null
        protected set(value) {
            field = value
            for (processor in ProcessorManager.processors) {
                if (processor.lock.isLocked) {
                    processor.condition.signalAll()
                }
            }
        }




    fun isMapped(): Boolean {
        return mapped != null
    }

}