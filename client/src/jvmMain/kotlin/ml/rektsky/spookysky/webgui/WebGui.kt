package ml.rektsky.spookysky.webgui



import ml.rektsky.spookysky.packets.Packet

object WebGui {

    const val port = 6930
    const val guiPort = 8040


    init {
        WebSocketServerImpl.start()
        HttpServerThread.start()
    }

    fun broadcastPacket(packet: Packet) {

    }

    fun onPacket(packet: Packet) {

    }

}