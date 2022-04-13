package ml.rektsky.spookysky.packets.impl.client

import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class PacketClientExecuteCommand(
    var command: String = ""
): Packet() {
    override fun read(data: FriendlyByteBuffer) {
        command = data.nextString()
    }

    override fun write(data: FriendlyByteBuffer) {
        data.putString(command)
    }
}