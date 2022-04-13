package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class KeybindSetting(): AbstractSetting<String, KeybindSetting>() {

    constructor(name: String) : this() {
        this.name = name
        this.value = "None"
    }


    override fun writeValue(target: FriendlyByteBuffer) {
        super.writeValue(target)
        target.putString(this.value!!)
    }

    override fun readValue(target: FriendlyByteBuffer) {
        super.readValue(target)
        this.value = target.nextString()
    }

    override fun copy(): KeybindSetting {
        return KeybindSetting().apply {
            this.name = this@KeybindSetting.name
            this.value = this@KeybindSetting.value
        }
    }

    override fun requiresUpdate(setting: AbstractSetting<*, *>): Boolean {
        return setting.value  != value
    }

}