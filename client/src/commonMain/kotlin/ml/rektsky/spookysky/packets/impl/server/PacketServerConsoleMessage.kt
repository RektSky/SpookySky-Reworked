package ml.rektsky.spookysky.packets.impl.server

import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class PacketServerConsoleMessage(
    var message: String = "",
    var color: Int = 0xffffff
): Packet() {

    override fun read(data: FriendlyByteBuffer) {
        message = data.nextString()
        color = data.nextInt()
    }

    override fun write(data: FriendlyByteBuffer) {
        data.putString(message)
        data.putInt(color)
    }

}