package ml.rektsky.spookysky.commands.impl

import ml.rektsky.spookysky.Client
import ml.rektsky.spookysky.commands.Command
import ml.rektsky.spookysky.utils.ChatColor
import ml.rektsky.spookysky.webgui.WebGuiInstance
import java.awt.Color
import java.io.*
import java.nio.charset.StandardCharsets
import java.util.zip.ZipInputStream


class CommandBypassStatus: Command(
    "bypassstatus",
    "Show the current bypass status by actually trying to detecting SpookySky",
    "bypassstatus"
) {
    override fun onCommand(sender: WebGuiInstance, args: Array<String>): Boolean {
        sender.sendMessage("Please wait a moment... it may take a while.")
        var detected = false
        try {
            val classPath = System.getProperty("java.class.path")
            val expectedClasses = ArrayList<String?>()
            for (s in classPath.split(":".toRegex()).toTypedArray()) {
                if (File(s).isDirectory) {
                    visit(File(s), "", expectedClasses)
                } else {
                    val inputStream = ZipInputStream(FileInputStream(s))
                    var entry = inputStream.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(".class")) {
                            val newName = entry.name.replace("/", ".")
                            expectedClasses.add(newName.substring(0, newName.length - 6))
                        }
                        inputStream.closeEntry()
                        entry = inputStream.nextEntry
                    }
                }
            }
            val allLoadedClasses: Array<Class<*>> = Client.instrumentation.getAllLoadedClasses()
            for (loadedClass in allLoadedClasses) {
                if (loadedClass.name.startsWith("[")) continue
                if (loadedClass.name.startsWith("sun.reflect.")) continue
                if (loadedClass.name.contains("$")) continue
                if (expectedClasses.contains(loadedClass.name) && loadedClass.name.contains("spookysky") || loadedClass.name.contains("javaagent")) {
                    sender.sendMessage(" // Bypassed loading ${loadedClass.name}", 0x5E5E5E)
                }
                if (!expectedClasses.contains(loadedClass.name) && (loadedClass.name.contains("spookysky") || loadedClass.name.contains("javaagent"))) {
                    detected = true
                    sender.sendMessage(" - FOUND HACK: ${loadedClass.name}", ChatColor.RED)
                }
            }
            sender.sendMessage("Unexpected Class Bypass: ${if (!detected) "UNDETECTED" else "DETECTED"}", if (!detected) ChatColor.GREEN else ChatColor.RED)
        } catch (e: Exception) {
            sender.sendMessage("Something went wrong while processing it!")
            sender.send(e)
        }
        return true
    }

    fun visit(file: File, prefix: String, expectedClass: ArrayList<String?>) {
        for (f in file.listFiles()!!) {
            if (f.isDirectory) {
                visit(f, prefix + f.name.toString() + ".", expectedClass)
            } else {
                if (f.name.endsWith(".class")) {
                    expectedClass.add(prefix + f.name.substring(0, f.name.length - 6))
                }
            }
        }
    }

}