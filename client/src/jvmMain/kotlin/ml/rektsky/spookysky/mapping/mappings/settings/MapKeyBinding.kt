package ml.rektsky.spookysky.mapping.mappings.settings

import ml.rektsky.spookysky.mapping.*

object MapKeyBinding: ClassMapping("KeyBinding") {

    val mapPressTime = FieldMapping(this, "pressTime")
    val mapPressed = FieldMapping(this, "pressed")
    val mapDescription = FieldMapping(this, "description")
    val mapCategory = FieldMapping(this, "category")


    val mapSetKeyBindState = MethodMapping(this, "setKeyBindState")

}

class KeyBinding(original: Any) {

    val description: String? by FieldMappingDelegation(original, MapKeyBinding.mapDescription)
    val category: String? by FieldMappingDelegation(original, MapKeyBinding.mapCategory)
    var pressed: Boolean? by FieldMappingDelegation(original, MapKeyBinding.mapPressed)
    var pressTime: Int? by FieldMappingDelegation(original, MapKeyBinding.mapPressTime)

    companion object {
        fun setKeyBindState(keyCode: Int, pressed: Boolean) =
            MethodMappingDelegation.invokeUnit(MapKeyBinding.mapSetKeyBindState, null, keyCode, pressed)

    }

}