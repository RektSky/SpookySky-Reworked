package ml.rektsky.spookysky.webgui

import ml.rektsky.spookysky.Client
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

object WebSocketServerImpl: WebSocketServer(InetSocketAddress("0.0.0.0", WebGui.port)) {
    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        Client.debug("A connection has created from ${conn?.remoteSocketAddress}")

    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        Client.debug("The connection with ${conn?.remoteSocketAddress} has been closed")
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        Client.debug("Got Message!")
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        Client.debug("Something went wrong while handling message!")
        if (Client.debug) {
            ex?.printStackTrace()
        }
    }

    override fun onStart() {
        Client.debug("WebSocket server has been started!")
    }
}