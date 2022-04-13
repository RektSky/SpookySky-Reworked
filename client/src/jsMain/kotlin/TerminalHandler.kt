import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.dom.create
import kotlinx.html.js.br
import kotlinx.html.js.li
import kotlinx.html.js.span
import ml.rektsky.spookysky.packets.Packet
import ml.rektsky.spookysky.packets.impl.client.PacketClientExecuteCommand
import ml.rektsky.spookysky.packets.impl.client.PacketClientRequestAutoComplete
import ml.rektsky.spookysky.packets.impl.server.PacketServerAutoCompleteResponse
import ml.rektsky.spookysky.packets.impl.server.PacketServerClearConsole
import ml.rektsky.spookysky.packets.impl.server.PacketServerConsoleMessage
import ml.rektsky.spookysky.utils.times
import org.w3c.dom.*
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import kotlin.js.Date

object TerminalHandler {

    const val openTerminalByDefault = false

    private val suggestionsElement = document.getElementById("suggestions") as HTMLUListElement
    private val terminalElement = document.getElementById("terminal") as HTMLDivElement
    private val terminalWindowElement = document.getElementById("terminal-window") as HTMLDivElement
    private val terminalOverlayElement = document.getElementById("terminal-overlay") as HTMLDivElement

    private val commandLineElement = document.getElementById("command-input") as HTMLInputElement

    private var lastAutoCompleteQuery = ""
    private var lastAutoCompleteResult = ArrayList<String>()
    private var autoCompleteSelection = ""

    private var currentHistoryIndex = -1
    private var history = ArrayList<String>()

    var opened = false
        set(value) {
            if (field != value) {
                field = value
                if (field) {
                    terminalElement.scroll(ScrollToOptions(top = terminalElement.scrollHeight.toDouble(), behavior = ScrollBehavior.INSTANT))
                    terminalWindowElement.hidden = false
                } else {
                    terminalWindowElement.hidden = true
                }
            }
        }

    init {
        println("[Terminal] Initializing...")
        window.addEventListener("keydown", listener@{
            if (it !is KeyboardEvent) return@listener
            if (it.keyCode == 9) {
                it.stopImmediatePropagation();
                it.preventDefault()
            }
        })
        window.addEventListener("keyup", listener@{
            if (it !is KeyboardEvent) return@listener
            if (it.keyCode == 9) {
                it.stopImmediatePropagation();
                it.preventDefault()
            }
        })
        window.addEventListener("mouseup", listener@{
            if (it !is KeyboardEvent) {
                return@listener
            }
            if (it.which == 9) {
                it.stopImmediatePropagation();
                it.preventDefault()
            }
        })
        commandLineElement.addEventListener("keydown", listener@{
            if (it !is KeyboardEvent) return@listener
            if (it.keyCode == 27) {
                autoCompleteSelection = ""
                suggestionsElement.hidden = true
                lastAutoCompleteResult = ArrayList()
                lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
                return@listener
            }
            if (it.keyCode == 32) {
                it.preventDefault()
                commandLineElement.value += " "
                autoCompleteSelection = ""
                suggestionsElement.hidden = true
                lastAutoCompleteResult = ArrayList()
                lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
                return@listener
            }
            if (it.keyCode == 9 && autoCompleteSelection == "" && lastAutoCompleteResult.size == 0) {
                lastAutoCompleteQuery = "*"*30 // Fetch it
                return@listener
            }
            if (it.keyCode == 13 && commandLineElement.value != "" && autoCompleteSelection == "") {
                sendCommand(commandLineElement.value)
                history.add(0, commandLineElement.value)
                currentHistoryIndex = -1
                commandLineElement.value = ""
                autoCompleteSelection = ""
                suggestionsElement.hidden = true
                lastAutoCompleteResult = ArrayList()
                lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
            }
            if ((it.keyCode == 13) && autoCompleteSelection != "") {
                it.stopImmediatePropagation();
                it.preventDefault()
                if (commandLineElement.value.endsWith(" ")) {
                    commandLineElement.value += autoCompleteSelection
                } else {
                    commandLineElement.value = commandLineElement.value.split(" ")
                        .let { it.subList(0, it.size - 1) }
                        .joinToString(" ") + (if (commandLineElement.value.split(" ").size > 1) " " else "") + autoCompleteSelection
                }
                autoCompleteSelection = ""
                suggestionsElement.hidden = true
                lastAutoCompleteResult = ArrayList()
                lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
            }
            if (it.keyCode == 38) {
                it.stopImmediatePropagation();
                it.preventDefault()
                if (suggestionsElement.hidden && history.size > 0) {
                    currentHistoryIndex = minOf(currentHistoryIndex + 1, history.size - 1)
                    println(currentHistoryIndex)
                    commandLineElement.value = history[currentHistoryIndex]
                    commandLineElement.focus()
                    commandLineElement.setSelectionRange(commandLineElement.value.length, commandLineElement.value.length)
                    autoCompleteSelection = ""
                    suggestionsElement.hidden = true
                    lastAutoCompleteResult = ArrayList()
                    lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
                    return@listener
                }
                if (lastAutoCompleteResult.size == 0) {
                    return@listener
                }
                var index = lastAutoCompleteResult.indexOf(autoCompleteSelection)
                index = if (index - 1 < 0) lastAutoCompleteResult.size - 1 else index - 1
                autoCompleteSelection = lastAutoCompleteResult[index]
                refreshSelected(index)
            }
            if (it.keyCode == 9 || it.keyCode == 40) {
                it.stopImmediatePropagation();
                it.preventDefault()
                if (suggestionsElement.hidden && history.size > 0 && it.keyCode == 40) {
                    if (currentHistoryIndex - 1 < 0) {
                        commandLineElement.value = ""
                        currentHistoryIndex = -1
                        return@listener
                    }
                    currentHistoryIndex = maxOf(0, currentHistoryIndex - 1)
                    println(currentHistoryIndex)
                    commandLineElement.value = history[currentHistoryIndex]
                    commandLineElement.focus()
                    commandLineElement.setSelectionRange(commandLineElement.value.length, commandLineElement.value.length)
                    autoCompleteSelection = ""
                    suggestionsElement.hidden = true
                    lastAutoCompleteResult = ArrayList()
                    lastAutoCompleteQuery = commandLineElement.value // Here we temporarily disable AutoComplete
                    return@listener
                }

                if (lastAutoCompleteResult.size == 0) {
                    return@listener
                }
                var index = lastAutoCompleteResult.indexOf(autoCompleteSelection)
                index = if (index + 1 >= lastAutoCompleteResult.size) 0 else index + 1
                autoCompleteSelection = lastAutoCompleteResult[index]
                refreshSelected(index)
            }
        })
        terminalOverlayElement.addEventListener("click", {
            opened = false
        })
        document.getElementById("open-terminal")!!.addEventListener("click", {
            opened = true
        })

        window.setInterval({
            if (commandLineElement.value != "" || true) {
                if (lastAutoCompleteQuery != commandLineElement.value) {
                    lastAutoCompleteQuery = commandLineElement.value
                    NetworkManager.sendPacket(PacketClientRequestAutoComplete(commandLineElement.value))
                }
            } else {
                lastAutoCompleteQuery = ""
                lastAutoCompleteResult.clear()
                autoCompleteSelection = ""
                suggestionsElement.hidden = true
            }
        })


        if (openTerminalByDefault) {
            opened = true
        }

        println("[Terminal] Terminal has been initialized!")
    }

    fun handlePacket(packet: Packet) {
        if (packet is PacketServerClearConsole) {
            clearConsole()
            return
        }
        if (packet is PacketServerConsoleMessage) {
            val date = Date()

            for (line in packet.message.lines()) {
                var hours = date.getHours().toString()
                hours = "0" * (2-hours.length) + hours
                var minutes = date.getMinutes().toString()
                minutes = "0" * (2-minutes.length) + minutes
                var seconds = date.getSeconds().toString()
                seconds = "0" * (2-seconds.length) + seconds
                addMessage("[REMOTE] [$hours:$minutes:$seconds] ", 0xffffff)
                addMessage("$line\n", packet.color)
            }
        }
        if (packet is PacketServerAutoCompleteResponse) {
            if (packet.suggestions.size == 0) {
                suggestionsElement.hidden = true
                lastAutoCompleteResult = ArrayList()
                autoCompleteSelection = ""
            } else {
                suggestionsElement.hidden = false
                suggestionsElement.innerHTML = ""
                var hasMatch: Boolean = false
                for (suggestion in packet.suggestions) {
                    var node = document.create.li {
                        +suggestion
                    }
                    node.addEventListener("click", {
                        autoCompleteSelection = suggestion
                        for (element in suggestionsElement.children.asList()) {
                            element.classList.remove("selected-suggestion")
                        }
                        node.classList.add("selected-suggestion")
                    })
                    suggestionsElement.append(node)
                    if (autoCompleteSelection == suggestion) {
                        node.classList.add("selected-suggestion")
                        hasMatch = true
                    }
                }
                if (!hasMatch) {
                    autoCompleteSelection = packet.suggestions[0]
                    refreshSelected(0)
                }
                lastAutoCompleteResult = packet.suggestions
            }
        }
    }

    fun addLocalMessage(message: String, color: Int = 0xffffff) {
        val date = Date()
        for (line in message.lines()) {
            var hours = date.getHours().toString()
            hours = "0" * (2-hours.length) + hours
            var minutes = date.getMinutes().toString()
            minutes = "0" * (2-minutes.length) + minutes
            var seconds = date.getSeconds().toString()
            seconds = "0" * (2-seconds.length) + seconds
            addMessage("[LOCAL ] [$hours:$minutes:$seconds] ", 0xffffff)
            addMessage("$line\n", color)
        }
    }

    fun sendCommand(command: String) {
        addLocalMessage(command, 0x646464)
        NetworkManager.sendPacket(PacketClientExecuteCommand(command))
    }

    fun clearConsole() {
        terminalElement.innerHTML = ""
    }

    fun addMessage(message: String, color: Int) {
        val message = message.replace("%BASE_URL%", window.location.hostname)
        var scroll = (terminalElement.scrollTop + terminalElement.offsetHeight) >= (terminalElement.scrollHeight)
        terminalElement.append(*createLine(message, color))
        if (scroll) {
            terminalElement.scroll(ScrollToOptions(top = terminalElement.scrollHeight.toDouble(), behavior = ScrollBehavior.INSTANT))
        }
    }

    private fun refreshSelected(index: Int) {
        for (i in 0 until suggestionsElement.children.asList().size) {
            var element = suggestionsElement.children[i]
            if (i != index) {
                element?.classList?.remove("selected-suggestion")
            } else {
                element?.classList?.add("selected-suggestion")
            }
        }
    }
    var httpRegex = Regex("^(http:\\/\\/www\\.|https:\\/\\/www\\.|http:\\/\\/|https:\\/\\/)?[a-z0-9]+([\\-\\.][a-z0-9]+)*\\.[a-z]{2,5}(:[0-9]{1,5})?(\\/.*)?\$")

    private fun createLine(message: String, color: Int): Array<HTMLElement> {
        val out = ArrayList<HTMLElement>()
        var index = 0
        for (line in message.lines()) {
            val element = document.create.span { +line }
            element.innerHTML = element.innerHTML.replace(" ", "&nbsp;").replace(httpRegex) {
                "<a ${it.value}>${it.value}</a>"
            }
            element.style.color = "#" + color.toString(16)
            out.add(element)
            index++
            if (index < message.lines().size) {
                out.add(document.create.br{})
            }
        }
        return out.toTypedArray()
    }

}

