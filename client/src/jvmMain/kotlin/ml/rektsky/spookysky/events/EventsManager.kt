package ml.rektsky.spookysky.events

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.WebGui
import java.lang.reflect.Method
import java.lang.reflect.Modifier

object EventsManager {

    private val listeners = HashMap<Any, ArrayList<Method>>()

    fun callEvent(event: Event) {
        for (listener in HashMap(listeners)) {
            for (method in ArrayList(listener.value)) {
                if (event.javaClass.isAssignableFrom(method.parameterTypes[0])) {
                    try {
                        method.invoke(listener.key, event)
                    } catch (e: Throwable) {
                        WebGui.message("Error while processing event!", ChatColor.RED)
                        Client.error(e)
                    }
                }
            }
        }
    }

    fun register(listener: Any) {
        listeners[listener] = ArrayList()
        for (method in listener.javaClass.methods) {
            if (!Modifier.isStatic(method.modifiers) && method.getAnnotationsByType(EventHandler::class.java).isNotEmpty()) {
                if (method.parameterCount == 1 && Event::class.java.isAssignableFrom(method.parameterTypes[0])) {
                    listeners[listener]!!.add(method)
                }
            }
        }
    }

    fun isRegistered(listener: Any): Boolean {
        return listener in listeners.keys
    }

    fun unregister(listener: Any) {
        listeners.remove(listener)
    }

}