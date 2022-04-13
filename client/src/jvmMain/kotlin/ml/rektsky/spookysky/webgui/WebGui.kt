package ml.rektsky.spookysky.webgui



import com.google.gson.GsonBuilder
import io.matthewnelson.component.base64.encodeBase64ToByteArray
import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.WebGuiPacketEvent
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketTextMessage
import ml.rektsky.spookysky.packets.impl.PacketUpdateModules

object WebGui {

    const val guiPort = 8040

    val gson = GsonBuilder()
        .create()
    val socketServer = WebSocketServerImpl(PacketManager.port)

    init {
        socketServer.isReuseAddr = true
        socketServer.start()
    }

    fun broadcastPacket(packet: Packet) {
        socketServer.broadcast(PacketManager.write(packet).encodeBase64ToByteArray())
    }

    fun message(message: String) {
        for (value in getConnectedClients()) {
            value.sendMessage(message)
        }
    }

    fun getConnectedClients(): Array<WebGuiInstance> {
        return socketServer.webGuiInstances.values.toTypedArray()
    }

    fun onPacket(packet: Packet, instance: WebGuiInstance) {
        if (packet is PacketTextMessage) {
            Client.debug("[WebGui] Got Message from Client: ${packet.message}")
        }
        WebGuiPacketEvent(packet, instance).callEvent()
    }


}

