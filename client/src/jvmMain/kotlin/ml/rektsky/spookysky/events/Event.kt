package ml.rektsky.spookysky.events

abstract class Event {

    fun callEvent() {
        EventsManager.callEvent(this)
    }

}