package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.FieldMappingDelegation
import ml.rektsky.spookysky.mapping.MethodMapping
import ml.rektsky.spookysky.mapping.mappings.settings.GameSettings

object MapMinecraft: ClassMapping("Minecraft") {

    val mapGetMinecraft = MethodMapping(this, "getMinecraft")

    val mapRunTick = MethodMapping(this, "runTick")

    val mapClickMouse = MethodMapping(this, "clickMouse")
    val mapRightClickMouse = MethodMapping(this, "rightClickMouse")

    val mapGameSettings = FieldMapping(this, "gameSettings")

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

    companion object {
        fun getMinecraft(): Minecraft? {
            val reflectiveMethod = MapMinecraft.mapGetMinecraft.getReflectiveMethod() ?: return null
            return Minecraft(reflectiveMethod.invoke(null))
        }
    }

}