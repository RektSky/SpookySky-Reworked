package ml.rektsky.spookysky.module

import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.module.settings.SettingsManager
import ml.rektsky.spookysky.module.settings.impl.KeybindSetting
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

open class AbstractModule() {


    open var name: String = "None"
    open var description: String = "None"
    open var category: Category = Category.MOVEMENT
    open var toggled: Boolean = false
    open var settings = ArrayList<AbstractSetting<*, *>>()

    init {

    }

    fun write(buffer: FriendlyByteBuffer) {
        buffer.putString(name)
        buffer.putString(description)
        buffer.putInt(category.ordinal)
        buffer.putBoolean(toggled)
        val settingsBuffer = ArrayList<(FriendlyByteBuffer) -> Unit>()
        for (setting in settings) {
            settingsBuffer.add { it ->
                it.putInt(SettingsManager.settings
                    .filter { v ->
                        v.value()::class.simpleName == setting::class.simpleName
                    }
                    .firstNotNullOf { e -> e.key }
                )
                setting.write(it)
            }
        }
        buffer.putList(settingsBuffer)
    }

    fun read(buffer: FriendlyByteBuffer) {
        name = buffer.nextString()
        description = buffer.nextString()
        category = Category.values()[buffer.nextInt()]
        toggled = buffer.nextBoolean()
        settings.clear()
        val nextList = buffer.nextList()
        for (settingBuffer in nextList) {
            val settingType = settingBuffer.nextInt()
            val setting = SettingsManager.settings[settingType]!!()
            setting.read(settingBuffer)
            settings.add(setting)
        }
    }

    fun copy(): AbstractModule {
        return AbstractModule().apply {
            name = this@AbstractModule.name
            description = this@AbstractModule.description
            category = this@AbstractModule.category
            toggled = this@AbstractModule.toggled
            settings = ArrayList()
            for (setting in this@AbstractModule.settings) {
                settings.add(setting.copy())
            }
        }
    }

    fun requiresUpdate(module: AbstractModule): Boolean {
        if (module.toggled != this.toggled) return true
        for (i in 0 until settings.size) {
            val setting = settings[i]
            if (setting.requiresUpdate(module.settings[i])) {
                return true
            }
        }
        return false
    }


}