package ml.rektsky.spookysky.events.types

import ml.rektsky.spookysky.events.Event

abstract class Cancellable(var cancelled: Boolean = false): Event() {



}