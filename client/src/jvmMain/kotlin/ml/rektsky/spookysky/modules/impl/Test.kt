package ml.rektsky.spookysky.modules.impl

import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.modules.Module
import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage
import ml.rektsky.spookysky.webgui.WebGui

class Test: Module("Test", "A module for testing in MOVEMENT category", Category.MOVEMENT) {

    val keyBindA: KeybindSetting = KeybindSetting("Active Key")



    override fun onDisable() {
        WebGui.broadcastPacket(PacketCommonTextMessage().apply { message = "Test has been Disabled!" })
    }

    override fun onEnable() {
        WebGui.broadcastPacket(PacketCommonTextMessage().apply { message = "Test has been Enabled!" })
    }
}