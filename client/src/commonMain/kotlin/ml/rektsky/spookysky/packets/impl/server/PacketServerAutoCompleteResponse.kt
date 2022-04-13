package ml.rektsky.spookysky.packets.impl.server

import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class PacketServerAutoCompleteResponse(
    var suggestions: ArrayList<String> = ArrayList()
): Packet() {

    constructor(vararg suggestions: String) : this(ArrayList(suggestions.asList()))

    override fun read(data: FriendlyByteBuffer) {
        for (friendlyByteBuffer in data.nextList()) {
            suggestions.add(friendlyByteBuffer.nextString())
        }
    }

    override fun write(data: FriendlyByteBuffer) {
        data.putList(suggestions.map { suggestion -> {buf -> buf.putString(suggestion)} })
    }
}