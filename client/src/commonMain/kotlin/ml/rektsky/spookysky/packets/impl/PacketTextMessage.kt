package ml.rektsky.spookysky.packets.impl

import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class PacketTextMessage: Packet() {

    var message = ""
    override fun read(data: FriendlyByteBuffer) {
        message = data.nextString()
    }

    override fun write(data: FriendlyByteBuffer) {
        data.putString(message)
    }


}