package ml.rektsky.spookysky.packets.impl

import ml.rektsky.spookysky.module.AbstractModule
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.utils.FriendlyByteBuffer

class PacketCommonUpdateModules: Packet() {

    var modules = ArrayList<AbstractModule>()

    override fun read(data: FriendlyByteBuffer) {
        for (friendlyByteBuffer in data.nextList()) {
            modules.add(AbstractModule().apply { read(friendlyByteBuffer) })
        }
    }

    override fun write(data: FriendlyByteBuffer) {
        val dataWriters = ArrayList<(FriendlyByteBuffer) -> Unit>()
        for (module in modules) {
            dataWriters.add {
                module.write(it)
            }
        }
        data.putList(dataWriters)
//        val tester = PacketCommonUpdateModules()
//        tester.read(FriendlyByteBuffer(data.getArray()))
//        for (moduleZip in tester.modules.zip(modules)) {
//            for (setting in moduleZip.first.settings.zip(moduleZip.second.settings)) {
//                println("From ${setting.first.value} to ${setting.second.value}")
//            }
//        }
    }


}