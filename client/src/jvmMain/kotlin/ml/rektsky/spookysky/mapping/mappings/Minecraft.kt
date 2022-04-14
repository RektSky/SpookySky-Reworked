package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object Minecraft: ClassMapping("Minecraft") {

    val mapGetMinecraft = MethodMapping(this, "getMinecraft")

    val mapRunTick = MethodMapping(this, "runTick")

    val mapClickMouse = MethodMapping(this, "clickMouse")
    val mapRightClickMouse = MethodMapping(this, "rightClickMouse")


    fun getMinecraft(): MinecraftWrapper? {
        val reflectiveMethod = mapGetMinecraft.getReflectiveMethod() ?: return null
        return MinecraftWrapper(reflectiveMethod.invoke(null))
    }

}

class MinecraftWrapper(val wrapped: Any) {

    fun clickMouse() {
        Minecraft.mapClickMouse.getReflectiveMethod()?.invoke(wrapped)
    }

    fun rightClickMouse() {
        Minecraft.mapRightClickMouse.getReflectiveMethod()?.invoke(wrapped)
    }

    fun runTick() {
        Minecraft.mapRunTick.getReflectiveMethod()?.invoke(wrapped)
    }

}