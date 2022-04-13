package ml.rektsky.spookysky.webgui

import io.matthewnelson.component.base64.decodeBase64ToArray
import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.packets.PacketManager
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress
import java.util.Base64

class WebSocketServerImpl(port: Int): WebSocketServer(InetSocketAddress("0.0.0.0", port)) {

    val webGuiInstances = HashMap<WebSocket, WebGuiInstance>()

    init {
        Client.debug("Attempting to start WebSocket Server")
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Client.debug("A connection has created from ${conn?.remoteSocketAddress}")
        webGuiInstances[conn!!] = WebGuiInstance(conn)
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Client.debug("The connection with ${conn?.remoteSocketAddress} has been closed")
        webGuiInstances.remove(conn!!)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        Client.debug("Got Message! Serializing... (${message})")
        WebGui.onPacket(PacketManager.read(message!!.decodeBase64ToArray()!!), webGuiInstances[conn]!!)
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Client.debug("Something went wrong while handling message!")
        if (Client.debug) {
            ex?.printStackTrace()
        }
    }

    override fun onStart() {
        Client.debug("WebSocket server has been started!")
        HttpServerThread.start()
    }
}