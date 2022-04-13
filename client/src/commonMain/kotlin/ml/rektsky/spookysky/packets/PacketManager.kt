package ml.rektsky.spookysky.packets

import ml.rektsky.spookysky.packets.impl.PacketTextMessage
import ml.rektsky.spookysky.packets.impl.PacketUpdateModules
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

object PacketManager {

    const val port = 16930
    private val registered = HashMap<Int, () -> Packet>()

    init {
        registerPacket { PacketTextMessage() }
        registerPacket { PacketUpdateModules() }
    }

    private fun registerPacket(supplier: () -> Packet) {
        registered[supplier()::class.simpleName.hashCode()] = supplier
    }

    fun read(bytes: ByteArray): Packet {
        val buffer = FriendlyByteBuffer(bytes)
        buffer.flip()
        val id = buffer.nextInt()
        val supplier = registered[id]
        val packet = supplier!!()
        packet.read(buffer)
        return packet
    }

    fun write(packet: Packet): ByteArray {
        val data = FriendlyByteBuffer(ByteArray(8192))
        val packetId = packet::class.simpleName.hashCode()
        if (registered[packetId] == null) {
            throw IllegalArgumentException("Cannot send unregistered packet!")
        }
        data.putInt(packetId)
        packet.write(data)
        return data.getArray()
    }

}