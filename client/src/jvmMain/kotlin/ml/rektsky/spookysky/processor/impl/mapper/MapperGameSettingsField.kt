package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.mapping.mappings.settings.MapGameSettings
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.utils.getFieldByName

class MapperGameSettingsField: Processor() {

    init {
        dependsOn(MapMinecraft)
        dependsOn(MapGameSettings)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (field in loadedClass.classNode.fields) {
            if (field.desc == "L${MapGameSettings.mapped!!.classNode.name};") {
                MapMinecraft.mapGameSettings.mapped = field
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapGameSettings.isMapped()
    }
}