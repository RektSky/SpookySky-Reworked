package ml.rektsky.spookysky.packets

import ml.rektsky.spookysky.packets.impl.PacketTextMessage

object PacketManager {

    private val registered = HashMap<Int, () -> Packet>()

    init {
        registerPacket { PacketTextMessage() }
    }

    private fun registerPacket(supplier: () -> Packet) {
        registered[supplier::class.simpleName.hashCode()] = supplier
    }

    fun read(data: Map<String, Any>): Packet {
        val supplier = registered[data["0packetType"] as Int]
        val packet = supplier!!()
        packet.read(data)
        return packet
    }

    fun write(packet: Packet): Map<String, Any> {
        val data = HashMap<String, Any>()
        val packetId = packet::class.simpleName.hashCode()
        if (registered[packetId] == null) {
            throw IllegalArgumentException("Cannot send unregistered packet!")
        }
        data["0packetType"] = packetId
        packet.write(data)
        return data
    }


}