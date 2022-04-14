import io.matthewnelson.component.base64.decodeBase64ToArray
import io.matthewnelson.component.base64.encodeBase64
import kotlinx.browser.window
import ml.rektsky.spookysky.module.AbstractModule
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.packets.PacketManager
import ml.rektsky.spookysky.packets.impl.PacketCommonTextMessage
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
import org.w3c.dom.WebSocket
import org.w3c.files.Blob
import kotlin.js.Promise

object NetworkManager {

    private var socket: WebSocket
    val modules = ArrayList<AbstractModule>()
    private val cachedModules = ArrayList<AbstractModule>()

    init {
        println("[SpookySky] Initializing Network Manager...")
        window.setInterval({
            updateModules()
        })
        socket = WebSocket("ws://${window.location.hostname}:${PacketManager.port}/")
        debug("[WEBSOCKET] Connecting to client via WebSocket: ${"ws://${window.location.hostname}:${PacketManager.port}/"}")
        socket.onopen = {
            debug("[WEBSOCKET] Successfully connected to client via WebSocket!")
            sendPacket(PacketCommonTextMessage().apply { message = "CLIENT_CONNECTION_HANDSHAKE" })
        }
        socket.onmessage = { it ->
            debug("[NetworkManager] Got packet: ${it.data.toString()}")
            if (it.data is Blob) {
                (js("it.data.text()") as Promise<String>).then { data ->
                    debug("Got blob message: ${data}")
                    onPacket(PacketManager.read(data))
                    debug("[NetworkManager] Processed packet: ${it.data.toString()}")
                }
//
            } else if (it.data is String) {
                onPacket(PacketManager.read((it.data as String)))
                debug("[NetworkManager] Processed packet: ${it.data.toString()}")
            } else {
                throw IllegalStateException("Got unknown websocket data! ")
            }
        }
    }

    fun updateModules() {
        val updates = ArrayList<AbstractModule>()
        if (cachedModules.size != modules.size) {
            resetCache()
            return
        }
        for (i in 0 until cachedModules.size) {
            val cached = cachedModules[i]
            val current = modules[i]
            if (current.requiresUpdate(cached)) {
                updates.add(current.copy())
            }
        }
        resetCache()
        if (updates.size == 0) return
        val updatePacket = PacketCommonUpdateModules().apply { this.modules.addAll(updates) }
        sendPacket(updatePacket)
    }

    fun sendPacket(packet: Packet) {
        debug("[NetworkManager] Attempting to send packet with type ${packet::class.simpleName}")
        val original = PacketManager.write(packet)
        val rawData = original
        if (!rawData.contentEquals(original)) {
            console.error("[NetworkManager] Assert failed! Original byte array is not equal to decoded.")
        }
        debug("[NetworkManager] Attempting to send $rawData")
        if (socket.readyState == WebSocket.OPEN) {
            socket.send(rawData)
            debug("[NetworkManager] Sent $rawData")
        } else {
            debug("[NetworkManager] Failed to send packet: ${packet::class.simpleName} because the websocket isn't opened or closed")
        }
    }

    private fun onPacket(packet: Packet) {
        debug("[NetworkManager] Processing packet: ${packet::class.simpleName}")
        TerminalHandler.handlePacket(packet)
        if (packet is PacketCommonUpdateModules) {
            for (module in packet.modules) {
                val targetModule: AbstractModule? = modules.firstOrNull { module.name == it.name }
                if (targetModule != null) {
                    modules.remove(targetModule)
                    modules.add(module.copy())
                    debug("[ModulesManager] Updated module: ${module.name}")
                } else {
                    modules.add(module.copy())
                    debug("[ModulesManager] Registered module: ${module.name}")
                }
            }
            resetCache()
            for (abstractModule in modules.filter { m -> m.category == Main.currentCategory }) {
                Renderer.updateModuleDisplay(abstractModule)
            }
        }
        if (packet is PacketCommonTextMessage) {
            println("[REMOTE] ${packet.message}")
        }
    }

    fun debug(string: String) {
        if (false) println(string)
    }

    private fun resetCache() {
        cachedModules.clear()
        for (module in modules) {
            cachedModules.add(module.copy())
        }
    }

}