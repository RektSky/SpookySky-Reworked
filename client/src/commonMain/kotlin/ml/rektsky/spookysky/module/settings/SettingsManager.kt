package ml.rektsky.spookysky.module.settings

import ml.rektsky.spookysky.module.settings.impl.PacketBooleanSetting
import ml.rektsky.spookysky.module.settings.impl.PacketKeybindSetting
import ml.rektsky.spookysky.module.settings.impl.PacketNumberSetting

object SettingsManager {

    val settings = HashMap<String, () -> PacketSetting<*, *>>()

    init {
        settings["boolean"] = {PacketBooleanSetting()}
        settings["number"] = {PacketNumberSetting()}
        settings["keybind"] = {PacketKeybindSetting()}
    }

}