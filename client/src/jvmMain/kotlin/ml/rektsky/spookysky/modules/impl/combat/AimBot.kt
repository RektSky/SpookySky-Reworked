package ml.rektsky.spookysky.modules.impl.combat

import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.impl.game.ClientTickEvent
import ml.rektsky.spookysky.events.impl.game.Render2DEvent
import ml.rektsky.spookysky.mapping.mappings.Minecraft
import ml.rektsky.spookysky.mapping.mappings.world.entity.Entity
import ml.rektsky.spookysky.module.Category
import ml.rektsky.spookysky.module.settings.impl.NumberSetting
import ml.rektsky.spookysky.modules.Module
import java.lang.Double.*
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.abs
import kotlin.math.max

class AimBot: Module("AimBot", "Aims automatically. You can go blatantly, and silently.", Category.COMBAT) {

    val turnSpeed = NumberSetting("Turn Speed", 40.0, 1.0, 5.0, 100.0)
    val range = NumberSetting("Range", 4, 0.1, 0, 10)
    val fov = NumberSetting("FOV", 69, 0.1, 5, 360)

    var target: Entity? = null
    var tick = 0

    override fun onDisable() {

    }

    override fun onEnable() {

    }

    @EventHandler
    fun onTick(event: ClientTickEvent) {
        tick++
        if (mc?.theWorld != null && mc?.thePlayer != null) {
            if (tick % 2 == 0) {
                val thePlayer = mc!!.thePlayer!!
                val sortedWith = mc!!.theWorld!!.loadedEntitiesList!!
                    .sortedWith(
                        Comparator.comparingDouble {
                            -it.distanceTo(
                                thePlayer.posX,
                                thePlayer.posY?.plus(1.53),
                                thePlayer.posZ
                            )
                        }
                    )
                val filter = sortedWith
                    .filter {
                        it.distanceTo(
                            thePlayer.posX,
                            thePlayer.posY?.plus(1.53),
                            thePlayer.posZ
                        ) < range.value!! && it.original != thePlayer.original
                    }
                filter
                    .forEach forEach@{ target ->
                        this.target = target
                        return@onTick
                    }
            }
        }
        target = null
    }

    @EventHandler
    fun onFrame(event: Render2DEvent) {
        if (mc?.theWorld != null && mc?.thePlayer != null) {
            if (target == null) return;
            val thePlayer = mc!!.thePlayer!!
            var yaw = (Math.toDegrees(
                Math.atan2(
                    mc!!.thePlayer!!.posZ!! - target!!.posZ!! + (ThreadLocalRandom.current().nextFloat() - 0.5) * 0,
                    mc!!.thePlayer!!.posX!! - target!!.posX!! + (ThreadLocalRandom.current().nextFloat() - 0.5) * 0
                )
            ) + 90).toFloat()
            if (yaw > 180) {
                yaw -= 360f
            }
            if (yaw < -180) {
                yaw += 360f
            }
            val rotationYaw = mc!!.thePlayer!!.rotationYaw!!
            val yawDiff = ((yaw - rotationYaw) % 360f + 540f) % 360f - 180f
            if (abs(yawDiff) > fov.value!!/2f) {
                return
            }
            thePlayer.rotationYaw = thePlayer.rotationYaw?.plus(((yawDiff)*(turnSpeed.value!!/100f)*(20f/Minecraft.debugFPS!!)).toFloat())
        }

    }


}