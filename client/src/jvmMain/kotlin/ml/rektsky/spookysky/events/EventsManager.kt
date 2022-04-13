package ml.rektsky.spookysky.events

import java.lang.reflect.Method
import java.lang.reflect.Modifier

object EventsManager {

    private val listeners = HashMap<Any, ArrayList<Method>>()

    fun callEvent(event: Event) {
        for (listener in listeners) {
            for (method in listener.value) {
                if (event.javaClass.isAssignableFrom(method.parameterTypes[0])) {
                    method.invoke(listener.key, event)
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