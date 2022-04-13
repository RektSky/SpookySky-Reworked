import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.html.dom.create
import kotlinx.html.js.a
import ml.rektsky.spookysky.module.Category
import org.w3c.dom.get

object Main {
    var currentCategory: Category = Category.MOVEMENT
        set(value) {
            field = value
            for (i in 0 until document.getElementById("categories")!!.childElementCount) {
                var categoryDisplay = document.getElementById("categories")!!.children[i]!!
                var category = Category.valueOf(categoryDisplay.attributes["category"]!!.value)
                categoryDisplay.classList.remove("clickable-category-button-selected")
                if (category == currentCategory) {
                    categoryDisplay.classList.add("clickable-category-button-selected")
                }
            }

            Renderer.clearModuleDisplay()
            for (module in NetworkManager.modules) {
                if (module.category == currentCategory) {
                    Renderer.updateModuleDisplay(module)
                }
            }
        }
}

fun main() {
    NetworkManager
    window.onload = {
        realMain()
    }
}

fun realMain() {
    for (value in Category.values()) {
        var element = document.create.a(classes = "clickable-category-button") {
            +value.displayName
        }
        element.setAttribute("category", value.name)
        document.getElementById("categories")!!.append(element)
        element.addEventListener("click", {event ->
            Main.currentCategory = value
        })
    }
    Main.currentCategory = Category.COMBAT
}


