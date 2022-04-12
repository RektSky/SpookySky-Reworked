import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    window.onload = {
        realMain()
    }

}

fun realMain() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    container.innerHTML = "<h1>Hello, World!</h1>"
}