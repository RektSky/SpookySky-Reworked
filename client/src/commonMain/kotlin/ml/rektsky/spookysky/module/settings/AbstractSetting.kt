package ml.rektsky.spookysky.module.settings

import ml.rektsky.spookysky.utils.FriendlyByteBuffer

abstract open class AbstractSetting<T, Self : AbstractSetting<T, Self>>() {

    var name: String = "None"
    var value: T? = null

    fun write(target: FriendlyByteBuffer) {
        writeValue(target)
    }
    fun read(target: FriendlyByteBuffer) {
        readValue(target)
    }

    protected open fun writeValue(target: FriendlyByteBuffer) {
        target.putString(name)
    }

    protected open fun readValue(target: FriendlyByteBuffer) {
        name = target.nextString()
    }

    abstract fun copy(): Self

    abstract fun requiresUpdate(setting: AbstractSetting<*, *>): Boolean

}