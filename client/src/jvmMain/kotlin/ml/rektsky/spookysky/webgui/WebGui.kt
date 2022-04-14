package ml.rektsky.spookysky.webgui



import com.google.gson.GsonBuilder
import io.matthewnelson.component.base64.encodeBase64ToByteArray
import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.impl.client.WebGuiPacketEvent
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage

object WebGui {

    const val guiPort = 8040

    val log = ArrayList<Message>()

    val gson = GsonBuilder()
        .create()
    val socketServer = WebSocketServerImpl(PacketManager.port)

    init {
        socketServer.isReuseAddr = true
        socketServer.start()
    }

    fun broadcastPacket(packet: Packet) {
        socketServer.broadcast(PacketManager.write(packet))
    }

    fun message(message: String, color: Int = 0xffffff, perm: Boolean = false) {
        if (perm) {
            log.add(Message(message, color))
        }
        for (value in getConnectedClients()) {
            value.sendMessage(message, color)
        }
    }

    fun getConnectedClients(): Array<WebGuiInstance> {
        return socketServer.webGuiInstances!!.values.toTypedArray()
    }

    fun onPacket(packet: Packet, instance: WebGuiInstance) {
        if (packet is PacketCommonTextMessage) {
            Client.addConsoleMessage("[WebGui] Got Message from Client: ${packet.message}")
        }
        WebGuiPacketEvent(packet, instance).callEvent()
    }



    data class Message(val message: String, val color: Int)

}

