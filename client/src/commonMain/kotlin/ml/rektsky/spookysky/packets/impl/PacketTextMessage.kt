package ml.rektsky.spookysky.packets.impl

import ml.rektsky.spookysky.packets.Packet

class PacketTextMessage: Packet() {

    var message = ""

    override fun read(data: Map<String, Any>) {
        message = data["message"] as String
    }

    override fun write(data: HashMap<String, Any>) {
        data["message"] = message
    }

}