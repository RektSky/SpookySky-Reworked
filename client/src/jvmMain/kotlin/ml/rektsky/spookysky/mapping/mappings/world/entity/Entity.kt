package ml.rektsky.spookysky.mapping.mappings.world.entity

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.FieldMappingDelegation
import kotlin.math.sqrt


object MapEntity : ClassMapping("Entity") {

    val mapPosX = FieldMapping(this, "posX")
    val mapPosY = FieldMapping(this, "posY")
    val mapPosZ = FieldMapping(this, "posZ")

    val mapMotionX = FieldMapping(this, "motionX")
    val mapMotionY = FieldMapping(this, "motionY")
    val mapMotionZ = FieldMapping(this, "motionZ")

    val mapRotationYaw = FieldMapping(this, "rotationYaw")
    val mapRotationPitch = FieldMapping(this, "rotationPitch")

    val mapFallDistance = FieldMapping(this, "fallDistance")

    val mapOnGround = FieldMapping(this, "onGround")
    val mapInvulnerable = FieldMapping(this, "invulnerable")

    val mapRidingEntity = FieldMapping(this, "ridingEntity")

}

open class Entity(val original: Any) {

    var posX: Double? by FieldMappingDelegation(original, MapEntity.mapPosX)
    var posY: Double? by FieldMappingDelegation(original, MapEntity.mapPosY)
    var posZ: Double? by FieldMappingDelegation(original, MapEntity.mapPosZ)

    var motionX: Double? by FieldMappingDelegation(original, MapEntity.mapMotionX)
    var motionY: Double? by FieldMappingDelegation(original, MapEntity.mapMotionY)
    var motionZ: Double? by FieldMappingDelegation(original, MapEntity.mapMotionZ)

    var rotationYaw: Float? by FieldMappingDelegation(original, MapEntity.mapRotationYaw)
    var rotationPitch: Float? by FieldMappingDelegation(original, MapEntity.mapRotationPitch)

    var fallDistance: Float? by FieldMappingDelegation(original, MapEntity.mapFallDistance)

    var onGround: Boolean? by FieldMappingDelegation(original, MapEntity.mapOnGround)
    var invulnerable: Boolean? by FieldMappingDelegation(original, MapEntity.mapInvulnerable)

    var ridingEntity: Entity? by FieldMappingDelegation(original, MapEntity.mapRidingEntity,
        { Entity(it) },
        { it!!.original })

    fun distanceTo(entity: Entity): Double {
        return distanceTo(entity.posX, entity.posY, entity.posZ)
    }
    
    fun distanceTo(x: Double?, y: Double?, z: Double?): Double {
        if (
            posX == null || x == null ||
            posY == null || y == null ||
            posZ == null || z == null
        ) return Double.MAX_VALUE
        val deltaX = posX!! - x
        val deltaY = posY!! - y
        val deltaZ = posZ!! - z
        return sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ)
    }

}
