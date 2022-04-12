package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.PacketSetting

class PacketNumberSetting(): PacketSetting<Number, PacketNumberSetting>() {

    var step: Number = 1
    var min: Number = 0
    var max: Number = 10

    override fun writeValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun readValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun copy(): PacketNumberSetting {
        return PacketNumberSetting().apply {
            this.name = this@PacketNumberSetting.name
            this.value = this@PacketNumberSetting.value
            this.step = this@PacketNumberSetting.step
            this.min = this@PacketNumberSetting.min
            this.max = this@PacketNumberSetting.max
        }
    }
}