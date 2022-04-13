import kotlinx.browser.document
import kotlinx.html.dom.create
import kotlinx.html.js.*
import kotlinx.html.*
import ml.rektsky.spookysky.module.AbstractModule
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.asList
import org.w3c.dom.get

object Renderer {

    val renderedElements = HashMap<String, HTMLDivElement>()

    private fun getModulesElement(): HTMLDivElement {
        return document.getElementById("right") as HTMLDivElement
    }

    private fun getModuleDisplay(module: AbstractModule, expanded: Boolean = false): HTMLDivElement {
        val element = document.create.div("module") {
            div("module-title") {
                h1 {+module.name}
                h2 {+module.description}
                h3(if (module.toggled) "enabled" else "disabled") {
                    +if (module.toggled) "Enabled" else "Disabled"
                }
                svg("arrow" + if(expanded) " expanded-arrow" else "") {
                    attributes["width"] = "11"
                    attributes["height"] = "16"
                    attributes["fill"] = "none"
                }
            }
            div("settings") {
                hidden = !expanded
            }
        }
        for (setting in module.settings) {
            element.getElementsByClassName("settings")[0]!!.append(SettingRenderer.render(setting))
        }
        element.getElementsByClassName("arrow")[0]?.innerHTML = """
                <path d="M0 8L10.5 0.205772L10.5 15.7942L0 8Z" fill="white"/>
        """.trimIndent()
        addModuleClickListener(element, module)

        element.setAttribute("module-name", module.name)
        return element
    }

    private fun addModuleClickListener(element: HTMLDivElement, module: AbstractModule) {
        element.getElementsByClassName("module-title")[0]!!.addEventListener("click", { event ->
            module.toggled = !module.toggled
            if (!module.toggled) {
                element.getElementsByClassName("enabled")[0]!!.className = "disabled"
                element.getElementsByClassName("disabled")[0]!!.innerHTML = "Disabled"
            } else {
                element.getElementsByClassName("disabled")[0]!!.className = "enabled"
                element.getElementsByClassName("enabled")[0]!!.innerHTML = "Enabled"
            }
        })
    }

    fun updateModuleDisplay(module: AbstractModule) {
        println("FINDING!")
        var htmlDivElement = renderedElements[module.name]
        if (htmlDivElement == null) {
            renderedElements[module.name] = getModuleDisplay(module)
            getModulesElement().append(renderedElements[module.name])
        } else {
            val newDisplay = getModuleDisplay(module,
                    !htmlDivElement.getElementsByClassName("settings")[0]!!
                        .hasAttribute("hidden"))
            htmlDivElement.remove()
            renderedElements[module.name] = newDisplay
            getModulesElement().append(newDisplay)
            println("FOUND!")
        }

    }

    fun clearModuleDisplay() {
        getModulesElement().innerHTML = ""
        renderedElements.clear()
    }




}