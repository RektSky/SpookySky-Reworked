import kotlinx.browser.document
import kotlinx.browser.window
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.PacketModule
import ml.rektsky.spookysky.module.settings.impl.PacketBooleanSetting
import ml.rektsky.spookysky.module.settings.impl.PacketKeybindSetting
import ml.rektsky.spookysky.module.settings.impl.PacketNumberSetting

fun main() {
    window.onload = {
        realMain()
    }

}

fun realMain() {
    var module = PacketModule()
    module.name = "Fly"
    module.description = "Allows you to fly like in creative mode"
    module.settings.clear()
    var element = PacketNumberSetting()
    element.name = "Speed"
    element.value = 6.9
    module.settings.add(element)

    var element1 = PacketBooleanSetting()
    element1.name = "Vanilla Bypass"
    element1.value = true
    module.settings.add(element1)

    var element2 = PacketKeybindSetting()
    element2.name = "Keybind"
    element2.value = "RSHIFT"
    module.settings.add(element2)
    Renderer.addModuleDisplay(module)
}


