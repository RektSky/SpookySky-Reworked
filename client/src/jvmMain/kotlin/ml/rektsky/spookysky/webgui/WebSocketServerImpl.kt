package ml.rektsky.spookysky.webgui

import io.matthewnelson.component.base64.decodeBase64ToArray
import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.packets.PacketManager
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

class WebSocketServerImpl(port: Int): WebSocketServer(InetSocketAddress("0.0.0.0", port)) {

    var webGuiInstances: HashMap<WebSocket, WebGuiInstance>? = HashMap()
    // excuse me, why is it null?? KOTLIN??

    init {
        Client.addConsoleMessage("Attempting to start WebSocket Server")
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Client.addConsoleMessage("A connection has created from ${conn?.remoteSocketAddress}")
        if (webGuiInstances == null) {
            webGuiInstances = HashMap()
        }
        webGuiInstances!![conn!!] = WebGuiInstance(conn)
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Client.addConsoleMessage("The connection with ${conn?.remoteSocketAddress} has been closed")
        if (webGuiInstances == null) {
            webGuiInstances = HashMap()
        }
        webGuiInstances!!.remove(conn!!)
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        if (webGuiInstances == null) {
            webGuiInstances = HashMap()
        }

        WebGui.onPacket(PacketManager.read(message!!), webGuiInstances!![conn]!!)
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Client.addConsoleMessage("Something went wrong while handling message!")
        if (Client.debug) {
            ex?.printStackTrace()
        }
    }

    override fun onStart() {
        Client.addConsoleMessage("WebSocket server has been started!")
        HttpServerThread.start()
    }
}