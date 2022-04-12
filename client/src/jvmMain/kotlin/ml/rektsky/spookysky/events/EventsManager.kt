package ml.rektsky.spookysky.events

import java.lang.reflect.Method
import java.lang.reflect.Modifier

object EventsManager {

    private val listeners = HashMap<Any, ArrayList<Method>>()

    fun callEvent(event: Event) {
        for (listener in listeners) {
            for (method in listener.value) {
                if (method.parameterTypes[0].isAssignableFrom(event.javaClass)) {
                    method.invoke(listener, event)
                }
            }
        }
    }

    fun register(listener: Any) {
        listeners[listener] = ArrayList()
        for (method in listener.javaClass.methods) {
            if (!Modifier.isStatic(method.modifiers)) {
                if (method.parameterCount == 1 && method.parameterTypes[0].isAssignableFrom(Event::class.java)) {
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