import kotlinx.browser.document
import kotlinx.html.dom.create
import kotlinx.html.js.*
import kotlinx.html.*
import ml.rektsky.spookysky.module.PacketModule
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.get

object Renderer {

    private fun getModulesElement(): HTMLDivElement {
        return document.getElementById("right") as HTMLDivElement
    }

    private fun getModuleDisplay(module: PacketModule): HTMLDivElement {
        val element = document.create.div("module") {
            div("module-title") {
                h1 {+module.name}
                h2 {+module.description}
                h3(if (module.toggled) "enabled" else "disabled") {
                    +if (module.toggled) "Enabled" else "Disabled"
                }
                svg("arrow") {
                    attributes["width"] = "11"
                    attributes["height"] = "16"
                    attributes["fill"] = "none"
                }
            }
            div("settings") {
                hidden = true
                for (setting in module.settings) {
                   SettingRenderer.render(setting, this)
                }
            }
        }
        element.addEventListener("click", { event ->
            module.toggled = !module.toggled
            if (element.getElementsByClassName("enabled")[0] != null) {
                element.getElementsByClassName("enabled")[0]!!.className = "disabled"
                element.getElementsByClassName("disabled")[0]!!.innerHTML = "Disabled"
            } else {
                element.getElementsByClassName("disabled")[0]!!.className = "enabled"
                element.getElementsByClassName("enabled")[0]!!.innerHTML = "Enabled"
            }
        })
        element.getElementsByClassName("arrow")[0]?.innerHTML = """
                <path d="M0 8L10.5 0.205772L10.5 15.7942L0 8Z" fill="white"/>
        """.trimIndent()

        element.setAttribute("module-name", module.name)
        return element
    }

    fun addModuleDisplay(module: PacketModule) {
        getModulesElement().append(getModuleDisplay(module))
    }

    fun clearModuleDisplay() {
        getModulesElement().innerHTML = ""
    }



}