import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketTextMessage
import ml.rektsky.spookysky.packets.impl.PacketUpdateModules
import java.util.Base64

fun main() {
    val modules = ModulesManager.getRegisteredModules()
    for (registeredModule in ModulesManager.getRegisteredModules()) {
        println("Trying to get module: ${registeredModule.name}")
        val packet =
            PacketManager.read(PacketManager.write(PacketUpdateModules().apply { this.modules.add(registeredModule) }))
        val packetUpdateModules = packet as PacketUpdateModules
        for (module in packetUpdateModules.modules) {
            println("Got module: ${module.name}")
        }
    }
}

