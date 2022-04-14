package ml.rektsky.spookysky.modules

import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.client.PreModuleToggleEvent
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.module.AbstractModule
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
import ml.rektsky.spookysky.webgui.WebGui


abstract class Module(
    override var name: String,
    override var description: String,
    override var category: Category,
): AbstractModule() {

    val mc: Minecraft?
        get() = Minecraft.getMinecraft()

    val keyBind: KeybindSetting = KeybindSetting("KeyBind")

    override var settings = ArrayList<AbstractSetting<*, *>>()

    override var toggled = false
        set(value) {
            WebGui.broadcastPacket(PacketCommonUpdateModules().apply { this.modules = ArrayList(listOf(copy())) })
            PreModuleToggleEvent(this, !field, value).callEvent()
            field = value
            if (value) {
                EventsManager.register(this)
                onEnable()
            } else {
                EventsManager.unregister(this)
                onDisable()
            }
        }

    protected abstract fun onDisable()
    protected abstract fun onEnable()


}


