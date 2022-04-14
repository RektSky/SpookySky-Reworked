package ml.rektsky.spookysky.mapping.mappings.gui

import ml.rektsky.spookysky.mapping.ClassMapping
import ml.rektsky.spookysky.mapping.MethodMapping

object MapGuiIngame: ClassMapping("GuiIngame") {

    val mapRenderGameOverlay = MethodMapping(this, "renderGameOverlay")

}
