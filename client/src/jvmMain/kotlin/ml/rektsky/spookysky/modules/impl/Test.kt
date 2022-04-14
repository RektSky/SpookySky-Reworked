package ml.rektsky.spookysky.modules.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.impl.game.ClientTickEvent
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.modules.Module
import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage
import ml.rektsky.spookysky.webgui.WebGui

class Test: Module("Test", "A module for testing in MISC category", Category.MISC) {

    @EventHandler
    fun onTick(event: ClientTickEvent) {
        WebGui.message("Tick!")
    }


    override fun onDisable() {
        WebGui.broadcastPacket(PacketCommonTextMessage().apply { message = "Test has been Disabled!" })
    }

    override fun onEnable() {
        WebGui.broadcastPacket(PacketCommonTextMessage().apply { message = "Test has been Enabled!" })
    }
}