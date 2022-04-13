package ml.rektsky.spookysky.packets

import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
import ml.rektsky.spookysky.packets.impl.client.PacketClientExecuteCommand
import ml.rektsky.spookysky.packets.impl.client.PacketClientRequestAutoComplete
import ml.rektsky.spookysky.packets.impl.server.PacketServerAutoCompleteResponse
import ml.rektsky.spookysky.packets.impl.server.PacketServerConsoleMessage
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

object PacketManager {

    const val port = 16930
    private val registered = HashMap<Int, () -> Packet>()

    init {
        registerPacket { PacketCommonTextMessage() }
        registerPacket { PacketCommonUpdateModules() }
        registerPacket { PacketClientExecuteCommand() }
        registerPacket { PacketClientRequestAutoComplete() }
        registerPacket { PacketServerAutoCompleteResponse() }
        registerPacket { PacketServerConsoleMessage() }
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