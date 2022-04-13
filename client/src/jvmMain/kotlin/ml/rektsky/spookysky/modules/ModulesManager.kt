package ml.rektsky.spookysky.modules

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.WebGuiPacketEvent
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.packets.impl.PacketUpdateModules
import ml.rektsky.spookysky.webgui.WebGui
import org.apache.logging.log4j.core.config.plugins.util.ResolverUtil
import java.net.URI

object ModulesManager {

    private val modules = ArrayList<Module>()

    init {
        val resolverUtil = ResolverUtil()
        resolverUtil.classLoader = this.javaClass.classLoader
        resolverUtil.findInPackage(object: ResolverUtil.Test {
            override fun matches(type: Class<*>?): Boolean {
                return type!!.superclass == Module::class.java
            }

            override fun matches(resource: URI?): Boolean {
                return true
            }

            override fun doesMatchClass(): Boolean {
                return true
            }

            override fun doesMatchResource(): Boolean {
                return false
            }

        }, Client::class.java.`package`.name)
        for (clazz in resolverUtil.classes) {
            Client.debug("Registered Module: ${clazz.simpleName}")
            modules.add((clazz as Class<*>).newInstance() as Module)
        }

        EventsManager.register(this)
    }

    fun getRegisteredModules(): List<Module> = ArrayList(modules)


    @EventHandler
    fun packetListener(event: WebGuiPacketEvent) {
        if (event.packet is PacketUpdateModules) {
            for (remoteModule in event.packet.modules) {
                val localModule = getRegisteredModules().firstOrNull {remoteModule.name == it.name}
                if (localModule == null) {
                    event.gui.sendMessage("Client just sent an invalid module update packet! Got name: ${remoteModule.name}")
                    return
                }
                if (localModule.toggled != remoteModule.toggled) {
                    WebGui.message("${remoteModule.name} has been ${if (remoteModule.toggled) "enabled" else "disabled"} by ${event.gui.getIP()}")
                    Client.debug("${remoteModule.name} has been ${if (remoteModule.toggled) "enabled" else "disabled"} by ${event.gui.getIP()}")
                    localModule.toggled = remoteModule.toggled
                }
                for (localRemoteSetting in localModule.settings.zip(remoteModule.settings)) {
                    val local: AbstractSetting<Any, *> = localRemoteSetting.first as AbstractSetting<Any, *>
                    val remote: AbstractSetting<Any, *> = localRemoteSetting.second as AbstractSetting<Any, *>
                    local.value = remote.value
                }
                WebGui.broadcastPacket(PacketUpdateModules().apply { modules = ArrayList(listOf(localModule.copy())) })
            }
        }
    }

}