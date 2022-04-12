package ml.rektsky.spookysky.module.settings

abstract open class PacketSetting<T, Self : PacketSetting<T, Self>>() {

    var name: String = "None"
    var value: T? = null

    fun write(target: HashMap<String, Any>) {
        target["type"] = SettingsManager.settings
        writeValue(target)
    }
    fun read(target: HashMap<String, Any>) {

    }

    protected open fun writeValue(target: HashMap<String, Any>) {

    }

    protected open fun readValue(target: HashMap<String, Any>) {

    }

    abstract fun copy(): Self

}