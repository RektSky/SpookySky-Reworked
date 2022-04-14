package ml.rektsky.spookysky.mapping.mappings.lwjgl

import ml.rektsky.spookysky.mapping.ClassMapping

object Mouse: ClassMapping("LWJGL Mouse") {

    fun isButtonDown(button: Int): Boolean {
        return Mouse.getReflectiveClass()!!.getDeclaredMethod("isButtonDown", Int::class.java).invoke(null, button) as Boolean
    }
}