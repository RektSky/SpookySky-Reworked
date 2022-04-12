package ml.rektsky.spookysky.packets

abstract class Packet() {

    abstract fun read(data: Map<String, Any>)
    abstract fun write(data: HashMap<String, Any>)

}