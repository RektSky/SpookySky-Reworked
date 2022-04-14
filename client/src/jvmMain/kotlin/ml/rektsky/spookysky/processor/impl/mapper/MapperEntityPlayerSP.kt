package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.world.entity.*
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.impl.templates.MapperStringSearcher
import ml.rektsky.spookysky.utils.MappingUtils

class MapperEntityPlayerSP: MapperStringSearcher("http://skins.minecraft.net/MinecraftSkins/%s.png", MapEntityPlayerSP) {

    override fun process0(loadedClass: LoadedClass) {
        if (MappingUtils.hasString("minecraft:brewing_stand", loadedClass.classNode) && MappingUtils.hasString("minecraft:beacon", loadedClass.classNode)) {
            mapping.mapped = loadedClass
            val classNode = MapEntityPlayerSP.mapped!!.classNode
            scheduleClassLoadAction(classNode.superName) {
                MapAbstractClientPlayer.mapped = it
                scheduleClassLoadAction(it.classNode.superName) {
                    MapEntityPlayer.mapped = it
                    scheduleClassLoadAction(it.classNode.superName) {
                        MapEntityLivingBase.mapped = it
                        scheduleClassLoadAction(it.classNode.superName) {
                            MapEntity.mapped = it
                        }
                    }
                }
            }
        }


    }
}