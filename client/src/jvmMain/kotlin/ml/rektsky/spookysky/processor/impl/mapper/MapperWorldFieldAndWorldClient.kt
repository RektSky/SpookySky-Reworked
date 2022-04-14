package ml.rektsky.spookysky.processor.impl.mapper

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.mapping.mappings.MapMinecraft
import ml.rektsky.spookysky.mapping.mappings.world.MapWorld
import ml.rektsky.spookysky.mapping.mappings.world.MapWorldClient
import ml.rektsky.spookysky.processor.LoadedClass
import ml.rektsky.spookysky.processor.Processor
import ml.rektsky.spookysky.utils.ChatColor

class MapperWorldFieldAndWorldClient: Processor() {

    init {
        dependsOn(MapWorld)
        dependsOn(MapMinecraft)
    }


    override fun shouldProcess(loadedClass: LoadedClass): Boolean {
        return loadedClass == MapMinecraft.mapped
    }

    override fun process0(loadedClass: LoadedClass) {
        for (field in loadedClass.classNode.fields) {
            if (field.desc.length > 2) {
                scheduleClassLoadAction(field.desc.let { it.substring(1, it.length - 1) }) {
                    if (it.classNode.superName == MapWorld.mapped!!.classNode.name) {
                        MapWorldClient.mapped = it
                        MapMinecraft.mapTheWorld.mapped = field
                    }
                }
            }
        }
    }

    override fun jobDone(): Boolean {
        return MapWorldClient.isMapped()
    }
}