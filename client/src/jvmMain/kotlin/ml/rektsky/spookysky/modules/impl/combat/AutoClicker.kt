package ml.rektsky.spookysky.modules.impl.combat

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.impl.game.Render2DEvent
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.mapping.mappings.lwjgl.Mouse
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.impl.NumberSetting
import ml.rektsky.spookysky.modules.Module
import ml.rektsky.spookysky.utils.Timer
import java.lang.Double.*
import java.lang.reflect.Field
import java.nio.ByteBuffer
import kotlin.random.Random

class AutoClicker: Module(
    "AutoClicker",
    "Click fast by holding down left click",
    Category.COMBAT
) {

    val cpsMinSetting: NumberSetting = NumberSetting("CPS Min", 10, 0.1, 1, 40)
    val cpsMaxSetting: NumberSetting = NumberSetting("CPS Max", 10, 0.1, 1, 40)

    private var cps = 0.0
    private var isClicked = false

    private var timer: Timer = Timer()
    private var cpsResetTimer: Timer = Timer()

    override fun onDisable() {

    }

    override fun onEnable() {

    }

    @EventHandler
    fun onUpdateMouse(event: Render2DEvent) {
        if (cpsResetTimer.checkAndReset(100)) {
            val range = max(cpsMinSetting.value!!.toDouble(), cpsMaxSetting.value!!.toDouble()) -
                    min(cpsMinSetting.value!!.toDouble(), cpsMaxSetting.value!!.toDouble())
            cps = (if(range != 0.0) Random(System.currentTimeMillis()).nextDouble(range)  else 0.0) +
                    min(cpsMinSetting.value!!.toDouble(), cpsMaxSetting.value!!.toDouble())
        }
        if (timer.checkAndReset((500 / cps).toLong())
            && Mouse.isButtonDown(0)) {
            click()
        }
    }

    fun click() {
        val bufferField: Field = Mouse.mapped!!.getReflectionClass().getDeclaredField("readBuffer")
        bufferField.isAccessible = true
        val buffer = bufferField[null] as ByteBuffer
        val newBuffer = ByteBuffer.allocate(1100)
        newBuffer.put(buffer)
        isClicked = !isClicked
        if (isClicked) {
            addToBuffer(newBuffer, 0, 1, 0, 0, 0)
        } else {
            addToBuffer(newBuffer, 0, 0, 0, 0, 0)
        }
        bufferField[null] = newBuffer
        Minecraft.getMinecraft()?.clickMouse()
    }

    private fun addToBuffer(buffer: ByteBuffer, button: Int, action: Int, cursorX: Int, cursorY: Int, wheel: Int) {
        buffer.put(button.toByte())
        buffer.put(action.toByte())
        buffer.putInt(cursorX)
        buffer.putInt(cursorY)
        buffer.putInt(wheel)
        buffer.putLong(System.nanoTime())
        buffer.flip()
    }


}