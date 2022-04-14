package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.mapping.mappings.world.entity.MapEntityPlayerSP
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor

class MapperThePlayer: Processor() {
    init {
        dependsOn(MapEntityPlayerSP)
        dependsOn(MapMinecraft)
    }

    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (field in loadedClass.classNode.fields) {
            if (field.desc.length > 2) {
                if (field.desc.let { it.substring(1, it.length - 1) } == MapEntityPlayerSP.mapped!!.classNode.name) {
                    MapMinecraft.mapThePlayer.mapped = field
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapMinecraft.mapThePlayer.isMapped()
    }
}