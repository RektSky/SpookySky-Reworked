package ml.rektsky.spookysky.modules

import ml.rektsky.spookysky.events.impl.PreModuleToggleEvent
import ml.rektsky.spookysky.module.AbstractModule
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting


abstract class Module(
    override var name: String,
    override var description: String,
    override var category: Category,
): AbstractModule() {


    val keyBind: KeybindSetting = KeybindSetting("KeyBind")

    override var settings = ArrayList<AbstractSetting<*, *>>()

    override var toggled = false
        set(value) {
            PreModuleToggleEvent(this, !field, value).callEvent()
            field = value
            if (value) onEnable() else onDisable()
        }

    protected abstract fun onDisable()
    protected abstract fun onEnable()


}


