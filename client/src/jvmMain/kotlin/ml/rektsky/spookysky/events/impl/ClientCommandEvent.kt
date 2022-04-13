package ml.rektsky.spookysky.events.impl

import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.events.types.Cancellable

class ClientCommandEvent(
    val command: Command,
    val args: Array<String>,
    val originalText: String
): Cancellable() {
}