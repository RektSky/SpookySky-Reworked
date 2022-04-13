package ml.rektsky.spookysky.mapping.mappings

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.FieldMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object MapMinecraft: ClassMapping("Minecraft") {

    val mapRunTick = MethodMapping(MapMinecraft, "runTick")

}