package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class NumberSetting(): AbstractSetting<Double, NumberSetting>() {

    constructor(name: String, value: Double, step: Double, min: Double, max: Double) : this() {
        this.name = name
        this.value = value
        this.step = step
        this.min = min
        this.max = max
    }

    constructor(name: String, value: Number, step: Number, min: Number, max: Number) : this(
        name, value.toDouble(), step.toDouble(), min.toDouble(), max.toDouble()
    )

    var step: Double = 1.toDouble()
    var min: Double = 0.toDouble()
    var max: Double = 10.toDouble()

    override fun writeValue(target: FriendlyByteBuffer) {
        super.writeValue(target)
        target.putDouble(this.value!!)
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