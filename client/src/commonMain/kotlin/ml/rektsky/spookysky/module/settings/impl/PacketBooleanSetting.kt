package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.PacketSetting

class PacketBooleanSetting: PacketSetting<Boolean, PacketBooleanSetting>() {
    override fun writeValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun readValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun copy(): PacketBooleanSetting {
        return PacketBooleanSetting().apply {
            this.name = this@PacketBooleanSetting.name
            this.value = this@PacketBooleanSetting.value
        }
    }

}