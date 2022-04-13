package ml.rektsky.spookysky.events.impl.client

import ml.rektsky.spookysky.events.Event
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.webgui.WebGuiInstance

class WebGuiPacketEvent(
    val packet: Packet,
    val sender: WebGuiInstance
): Event()