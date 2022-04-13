package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class NumberSetting(): AbstractSetting<Number, NumberSetting>() {

    constructor(name: String, value: Number, step: Number, min: Number, max: Number) : this() {
        this.name = name
        this.value = value
        this.step = step
        this.min = min
        this.max = max
    }

    var step: Number = 1.toDouble()
    var min: Number = 0.toDouble()
    var max: Number = 10.toDouble()

    override fun writeValue(target: FriendlyByteBuffer) {
        super.writeValue(target)
        target.putDouble(this.value!!.toDouble())
        target.putDouble(this.step.toDouble())
        target.putDouble(this.min.toDouble())
        target.putDouble(this.max.toDouble())
    }

    override fun readValue(target: FriendlyByteBuffer) {
        super.readValue(target)
        this.value = target.nextDouble()
        this.step = target.nextDouble()
        this.min = target.nextDouble()
        this.max = target.nextDouble()
    }

    override fun copy(): NumberSetting {
        return NumberSetting().apply {
            this.name = this@NumberSetting.name
            this.value = this@NumberSetting.value
            this.step = this@NumberSetting.step
            this.min = this@NumberSetting.min
            this.max = this@NumberSetting.max
        }
    }

    override fun requiresUpdate(setting: AbstractSetting<*, *>): Boolean {
        return setting.value  != value
    }
}