package ml.rektsky.spookysky.module

import ml.rektsky.spookysky.module.settings.PacketSetting
import ml.rektsky.spookysky.module.settings.impl.PacketKeybindSetting

class PacketModule() {


    var name: String = "None"
    var description: String = "None"
    var category: Category = Category.MOVEMENT
    var toggled: Boolean = false
    var settings: ArrayList<PacketSetting<*, *>>

    init {
        var keybindSetting = PacketKeybindSetting()
        keybindSetting.name = "Keybind"
        keybindSetting.value = "None"
        settings = ArrayList(
            listOf(keybindSetting)
        )
    }

    fun write(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    fun read(target: HashMap<String, Any>) {
        TODO("Not yet implemented")
    }

    fun copy(): PacketModule {
        return PacketModule().apply {
            name = this@PacketModule.name
            description = this@PacketModule.description
            category = this@PacketModule.category
            toggled = this@PacketModule.toggled
            settings = ArrayList()
            for (setting in this@PacketModule.settings) {
                settings.add(setting.copy())
            }
        }
    }


}