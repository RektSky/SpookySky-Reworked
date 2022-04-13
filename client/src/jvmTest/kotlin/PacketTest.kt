import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules

fun main() {
    val modules = ModulesManager.getRegisteredModules()
    for (registeredModule in ModulesManager.getRegisteredModules()) {
        println("Trying to get module: ${registeredModule.name}")
        val packet =
            PacketManager.read(PacketManager.write(PacketCommonUpdateModules().apply { this.modules.add(registeredModule) }))
        val packetCommonUpdateModules = packet as PacketCommonUpdateModules
        for (module in packetCommonUpdateModules.modules) {
            println("Got module: ${module.name}")
        }
    }
}

