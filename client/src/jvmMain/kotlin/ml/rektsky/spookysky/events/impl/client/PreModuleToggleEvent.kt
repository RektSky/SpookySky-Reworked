package ml.rektsky.spookysky.events.impl.client

import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.events.types.Cancellable
import ml.rektsky.spookysky.modules.Module

class PreModuleToggleEvent(
    val module: Module,
    val before: Boolean,
    val after: Boolean,
): Cancellable()