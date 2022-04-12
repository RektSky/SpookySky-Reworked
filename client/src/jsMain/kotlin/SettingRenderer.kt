import kotlinx.browser.document
import kotlinx.html.*
import kotlinx.html.dom.*
import ml.rektsky.spookysky.module.settings.PacketSetting
import ml.rektsky.spookysky.module.settings.impl.PacketBooleanSetting
import ml.rektsky.spookysky.module.settings.impl.PacketKeybindSetting
import ml.rektsky.spookysky.module.settings.impl.PacketNumberSetting
import org.w3c.dom.HTMLDivElement

object SettingRenderer {

    fun render(setting: PacketSetting<*, *>, div: DIV) {
        if (setting is PacketKeybindSetting) {
            renderKeybindSetting(setting, div)
        }
        if (setting is PacketNumberSetting) {
            renderNumberSetting(setting, div)
        }
        if (setting is PacketBooleanSetting) {
            renderBooleanSetting(setting, div)
        }
    }

    private fun renderKeybindSetting(setting: PacketKeybindSetting, div: DIV) {
        div.div("setting") {
            p { +setting.name }
            input(InputType.text, classes = "setting-input keybind-setting")
        }
    }

    private fun renderNumberSetting(setting: PacketNumberSetting, div: DIV) {
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

    private fun renderBooleanSetting(setting: PacketBooleanSetting, div: DIV) {
        div.div("setting") {
            p { +setting.name }
            div(classes = "setting-input boolean-setting boolean-setting-disabled")

        }
    }



}