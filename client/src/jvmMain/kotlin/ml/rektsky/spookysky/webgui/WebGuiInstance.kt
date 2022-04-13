package ml.rektsky.spookysky.webgui

import io.matthewnelson.component.base64.encodeBase64
import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.modules.ModulesManager
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
import ml.rektsky.spookysky.packets.impl.server.PacketServerConsoleMessage
import ml.rektsky.spookysky.utils.FriendlyByteBuffer
import org.java_websocket.WebSocket
import java.net.InetSocketAddress

class WebGuiInstance(private val socket: WebSocket) {


    init {
        for (registeredModule in ModulesManager.getRegisteredModules()) {
            send(PacketCommonUpdateModules().apply { modules.add(registeredModule) })
        }

        Client.debug("[${socket.remoteSocketAddress}] Sent modules update to client!")
        sendMessage("Connected as $${getIP()}")
    }

    fun send(packet: Packet) {
        Client.debug("[${socket.remoteSocketAddress}] Sending ${FriendlyByteBuffer(ByteArray(8192)).apply { packet.write(this) }.getArray().encodeBase64()}")
        socket.send(PacketManager.write(packet).encodeBase64())
    }

    fun close() {
        socket.close()
    }

    fun getIP(): InetSocketAddress {
        return socket.remoteSocketAddress
    }

    fun sendMessage(message: String, color: Int = 0xffffff) {
        send(PacketCommonTextMessage().apply { this.message = message})
        send(PacketServerConsoleMessage(message, color))
    }


}