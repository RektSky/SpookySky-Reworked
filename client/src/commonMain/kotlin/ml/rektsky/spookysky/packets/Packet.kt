package ml.rektsky.spookysky.packets

import ml.rektsky.spookysky.utils.FriendlyByteBuffer

abstract class Packet() {

    abstract fun read(data: FriendlyByteBuffer)
    abstract fun write(data: FriendlyByteBuffer)

}