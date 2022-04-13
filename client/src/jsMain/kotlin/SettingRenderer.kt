import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.*
import kotlinx.html.dom.create
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.module.settings.impl.BooleanSetting
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.module.settings.impl.NumberSetting
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.get

object SettingRenderer {

    fun render(setting: AbstractSetting<*, *>): HTMLElement {
        if (setting is KeybindSetting) {
            return renderKeybindSetting(setting)
        }
        else if (setting is NumberSetting) {
            return renderNumberSetting(setting)
        }
        else if (setting is BooleanSetting) {
            return renderBooleanSetting(setting)
        } else {
            throw IllegalArgumentException("Unsupported Setting Type")
        }
    }

    private fun renderKeybindSetting(setting: KeybindSetting): HTMLElement {
        return document.create.div("setting") {
            p { +setting.name }
            input(InputType.text, classes = "setting-input keybind-setting") {
                value = setting.value?:"None"
            }
            attributes["setting-name"] = setting.name
        }.apply {
            window.setInterval({})
            addEventListener("change", {
                setting.value = (getElementsByClassName("keybind-setting")[0] as HTMLInputElement).value
            })
        }
    }

    private fun renderNumberSetting(setting: NumberSetting): HTMLElement {
         return document.create.div("setting") {
            p { +setting.name }
            input(InputType.range, classes = "setting-input number-setting") {
                value = setting.value.toString()
                min = setting.min.toString()
                max = setting.max.toString()
                step = setting.step.toString()
            }
            attributes["setting-name"] = setting.name
        }.apply {
             addEventListener("change", {
                 setting.value = (getElementsByClassName("number-setting")[0] as HTMLInputElement).valueAsNumber
             })
         }
    }

    private fun renderBooleanSetting(setting: BooleanSetting): HTMLElement {
        return document.create.div("setting") {
            p { +setting.name }
            div(classes = "setting-input boolean-setting " + if (setting.value == true) "boolean-setting-enabled" else "boolean-setting-disabled")
            attributes["setting-name"] = setting.name
        }.apply {
            addEventListener("click", {
                setting.value = "enabled" in getElementsByClassName("boolean-setting")[0]!!.className;
            })
        }
    }



}