package ml.rektsky.spookysky.modules.impl

import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.modules.Module
import ml.rektsky.spookysky.packets.impl.PacketTextMessage
import ml.rektsky.spookysky.webgui.WebGui

class Test: Module("Test", "A module for testing in MOVEMENT category", Category.MOVEMENT) {
    override fun onDisable() {
        WebGui.broadcastPacket(PacketTextMessage().apply { message = "Test has been Disabled!" })
    }

    override fun onEnable() {
        WebGui.broadcastPacket(PacketTextMessage().apply { message = "Test has been Enabled!" })
    }
}