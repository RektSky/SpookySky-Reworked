package ml.rektsky.spookysky.mapping.mappings.world

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.FieldMappingDelegation
import ml.rektsky.spookysky.mapping.MethodMapping
import ml.rektsky.spookysky.mapping.mappings.world.entity.Entity

object MapWorld: ClassMapping("World") {

    val mapSetBlockState = MethodMapping(this, "setBlockState")
    val mapPlayMoodSoundAndCheckLight = MethodMapping(this, "playMoodSoundAndCheckLight")

    val mapLoadedEntitiesList = FieldMapping(this, "loadedEntitiesList")

}


open class World(val original: Any) {

    val loadedEntitiesList: ArrayList<Entity>?
        by FieldMappingDelegation(original, MapWorld.mapLoadedEntitiesList, { original ->
            ArrayList(arrayListOf(*(original as List<Any>).map { Entity(it) }.toTypedArray()))
        })

}