package ml.rektsky.spookysky.module.settings.impl

import ml.rektsky.spookysky.module.settings.PacketSetting

class PacketKeybindSetting: PacketSetting<String, PacketKeybindSetting>() {
    override fun writeValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun readValue(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    override fun copy(): PacketKeybindSetting {
        return PacketKeybindSetting().apply {
            this.name = this@PacketKeybindSetting.name
            this.value = this@PacketKeybindSetting.value
        }
    }


}