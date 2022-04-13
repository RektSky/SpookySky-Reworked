package ml.rektsky.spookysky.module.settings

import ml.rektsky.spookysky.module.settings.impl.BooleanSetting
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.module.settings.impl.NumberSetting

object SettingsManager {

    val settings = HashMap<Int, () -> AbstractSetting<*, *>>()

    init {
        settings["boolean".hashCode()] = {BooleanSetting()}
        settings["number".hashCode()] = {NumberSetting()}
        settings["keybind".hashCode()] = {KeybindSetting()}
    }

}