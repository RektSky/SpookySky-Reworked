package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class BooleanSetting(): AbstractSetting<Boolean, BooleanSetting>() {

    constructor(name: String, defaultValue: Boolean) : this() {
        this.name = name
        this.value = defaultValue
    }

    override fun writeValue(target: FriendlyByteBuffer) {
        super.writeValue(target)
        target.putBoolean(this.value!!)
    }

    override fun readValue(target: FriendlyByteBuffer) {
        super.readValue(target)
        this.value = target.nextBoolean()
    }

    override fun copy(): BooleanSetting {
        return BooleanSetting().apply {
            this.name = this@BooleanSetting.name
            this.value = this@BooleanSetting.value
        }
    }

    override fun requiresUpdate(setting: AbstractSetting<*, *>): Boolean {
        return setting.value  != value
    }

}