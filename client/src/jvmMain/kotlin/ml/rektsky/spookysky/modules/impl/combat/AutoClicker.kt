package ml.rektsky.spookysky.modules.impl.combat

import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.modules.Module
import java.lang.reflect.Field
import java.nio.ByteBuffer

class AutoClicker: Module(
    "AutoClicker",
    "Click fast by holding down left click",
    Category.COMBAT
) {

    private val cps = 0.0
    private var isClicked = false

    override fun onDisable() {

    }

    override fun onEnable() {

    }

    fun click() {
//        val bufferField: Field = Mouse.getMouseClass().getDeclaredField("readBuffer")
//        bufferField.setAccessible(true)
//        val buffer: ByteBuffer = bufferField.get(null) as ByteBuffer // Static field
//
//        val newBuffer: ByteBuffer = ByteBuffer.allocate(102400)
//        newBuffer.put(buffer)
//        newBuffer.put(0.toByte()) // Left click
//
//        newBuffer.put((if (!isClicked.also { isClicked = it }) 0 else 1).toByte())
//        newBuffer.putInt(0) // Dynamic X
//
//        newBuffer.putInt(0) // Dynamic Y
//
//        newBuffer.putInt(0) // Dynamic Wheel
//
//        newBuffer.putLong(System.nanoTime()) // event_nanos
//
//        newBuffer.flip()
//        bufferField.set(null, newBuffer)
    }

}