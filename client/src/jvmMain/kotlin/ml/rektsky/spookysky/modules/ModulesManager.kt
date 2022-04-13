package ml.rektsky.spookysky.modules

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.events.EventHandler
import ml.rektsky.spookysky.events.EventsManager
import ml.rektsky.spookysky.events.impl.WebGuiPacketEvent
import ml.rektsky.spookysky.module.settings.AbstractSetting
import ml.rektsky.spookysky.packets.impl.PacketCommonUpdateModules
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
            Client.addConsoleMessage("[Modules Manager] Registered Module: ${clazz.simpleName}")
            val module = (clazz as Class<*>).newInstance() as Module
            modules.add(module)
            var superclass: Class<*>? = module.javaClass
            while (superclass != null) {
                for (field in superclass.declaredFields) {
                    if (AbstractSetting::class.java.isAssignableFrom(field.type)) {
                        field.isAccessible = true
                        module.settings.add(field.get(module) as AbstractSetting<*, *>)
                    }
                }
                superclass = superclass.superclass
            }

        }

        EventsManager.register(this)
    }

    fun getRegisteredModules(): List<Module> = ArrayList(modules)


    @EventHandler
    fun packetListener(event: WebGuiPacketEvent) {
        if (event.packet is PacketCommonUpdateModules) {
            for (remoteModule in event.packet.modules) {
                val localModule = getRegisteredModules().firstOrNull {remoteModule.name == it.name}
                if (localModule == null) {
                    event.sender.sendMessage("Client just sent an invalid module update packet! Got name: ${remoteModule.name}")
                    return
                }
                if (localModule.toggled != remoteModule.toggled) {
                    WebGui.message("${remoteModule.name} has been ${if (remoteModule.toggled) "enabled" else "disabled"} by ${event.sender.getIP()}")
                    Client.addConsoleMessage("${remoteModule.name} has been ${if (remoteModule.toggled) "enabled" else "disabled"} by ${event.sender.getIP()}")
                    localModule.toggled = remoteModule.toggled
                }
                for (localRemoteSetting in localModule.settings.zip(remoteModule.settings)) {
                    val local: AbstractSetting<Any, *> = localRemoteSetting.first as AbstractSetting<Any, *>
                    val remote: AbstractSetting<Any, *> = localRemoteSetting.second as AbstractSetting<Any, *>
                    local.value = remote.value
                }
                WebGui.broadcastPacket(PacketCommonUpdateModules().apply { modules = ArrayList(listOf(localModule.copy())) })
            }
        }
    }

}