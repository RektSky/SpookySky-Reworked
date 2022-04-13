import kotlinx.html.*
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.module.settings.impl.BooleanSetting
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.module.settings.impl.NumberSetting

object SettingRenderer {

    fun render(setting: AbstractSetting<*, *>, div: DIV) {
        if (setting is KeybindSetting) {
            renderKeybindSetting(setting, div)
        }
        if (setting is NumberSetting) {
            renderNumberSetting(setting, div)
        }
        if (setting is BooleanSetting) {
            renderBooleanSetting(setting, div)
        }
    }

    private fun renderKeybindSetting(setting: KeybindSetting, div: DIV) {
        div.div("setting") {
            p { +setting.name }
            input(InputType.text, classes = "setting-input keybind-setting") {
                value = setting.value?:"None"
            }
        }
    }

    private fun renderNumberSetting(setting: NumberSetting, div: DIV) {
        div.div("setting") {
            p { +setting.name }
            input(InputType.range, classes = "setting-input number-setting") {
                value = setting.value.toString()
                min = setting.min.toString()
                max = setting.max.toString()
                step = setting.step.toString()
            }
        }
    }

    private fun renderBooleanSetting(setting: BooleanSetting, div: DIV) {
        div.div("setting") {
            p { +setting.name }
            div(classes = "setting-input boolean-setting " + if (setting.value == true) "boolean-setting-enabled" else "boolean-setting-disabled")
        }
    }



}