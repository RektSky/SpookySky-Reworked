package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.FieldMappingDelegation
import ml.rektsky.spookysky.mapping.MethodMapping
import ml.rektsky.spookysky.mapping.mappings.settings.GameSettings
import ml.rektsky.spookysky.mapping.mappings.world.World
import ml.rektsky.spookysky.mapping.mappings.world.entity.EntityPlayerSP

object MapMinecraft: ClassMapping("Minecraft") {

    val mapGetMinecraft = MethodMapping(this, "getMinecraft")

    val mapRunTick = MethodMapping(this, "runTick")

    val mapClickMouse = MethodMapping(this, "clickMouse")
    val mapRightClickMouse = MethodMapping(this, "rightClickMouse")

    val mapGameSettings = FieldMapping(this, "gameSettings")
    val mapTheWorld = FieldMapping(this, "theWorld")
    val mapThePlayer = FieldMapping(this, "thePlayer")

    val mapDebugFPS = FieldMapping(this, "debugFPS")

}

class Minecraft(val original: Any) {

    fun clickMouse() {
        MapMinecraft.mapClickMouse.getReflectiveMethod()?.invoke(original)
    }

    fun rightClickMouse() {
        MapMinecraft.mapRightClickMouse.getReflectiveMethod()?.invoke(original)
    }

    fun runTick() {
        MapMinecraft.mapRunTick.getReflectiveMethod()?.invoke(original)
    }



    val gameSettings: GameSettings?
        by FieldMappingDelegation(original, MapMinecraft.mapGameSettings, { GameSettings(it) }, { it.original })

    val theWorld: World?
            by FieldMappingDelegation(original, MapMinecraft.mapTheWorld, { World(it) }, { it.original })

    val thePlayer: EntityPlayerSP?
            by FieldMappingDelegation(original, MapMinecraft.mapThePlayer, { EntityPlayerSP(it) }, { it.original })

    companion object {
        fun getMinecraft(): Minecraft? {
            val reflectiveMethod = MapMinecraft.mapGetMinecraft.getReflectiveMethod() ?: return null
            return Minecraft(reflectiveMethod.invoke(null))
        }

        val debugFPS: Int?
                by FieldMappingDelegation(null, MapMinecraft.mapDebugFPS)
    }

}