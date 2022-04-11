package ml.rektsky.spookysky

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.LdcInsnNode
import sun.jvmstat.monitor.HostIdentifier
import sun.jvmstat.monitor.MonitoredHost
import sun.jvmstat.monitor.MonitoredVmUtil
import sun.jvmstat.monitor.VmIdentifier
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.swing.JOptionPane
import kotlin.collections.ArrayList
import kotlin.system.exitProcess


object Main {

    const val gsonInjectionStart = "de8dc0c0-58bc-4ebb-95bc-95cb44b90ce9"
    const val gsonInjectionEnd = "3e9e7328-6202-431e-8ad5-e2e1dbf5b0c7"

    const val text = "Thanks for using SpookySky! You can start Minecraft now.\n" +
            "After pressing OK, It will restart your minecraft in order to do screen-sharing bypass\n" +
            "WARNING: Don't close it until the minecraft has been launched."

    val windowsFlag = System.getProperty("os.name").lowercase(Locale.ENGLISH).contains("windows")

    @JvmStatic
    fun main(args: Array<String>) {
        Main.javaClass.classLoader.getResource(".")
        Main.javaClass.classLoader.getResourceAsStream("agent.jar")

        JOptionPane.showConfirmDialog(null, text,
            "SpookySky Injector", JOptionPane.CLOSED_OPTION, JOptionPane.PLAIN_MESSAGE)


        println("Checking if there's any running Minecraft instance...")
        val monitoredHost = MonitoredHost.getMonitoredHost(HostIdentifier("localhost"))
        for (pid in monitoredHost.activeVms()) {
            val vm = monitoredHost.getMonitoredVm(VmIdentifier("//$pid?mode=r"), 0)
            val mainArgs = MonitoredVmUtil.mainArgs(vm)
            val jvmArgs = MonitoredVmUtil.jvmArgs(vm)
            if (mainArgs == null || jvmArgs == null) continue
            if ("-accessToken" !in mainArgs) {
                continue
            }
            println("Found Minecraft! Killing it...")
            if (windowsFlag) {
                Runtime.getRuntime().exec("TASKKILL /F /PID $pid").waitFor()
            } else {
                Runtime.getRuntime().exec("kill -9 $pid").waitFor()
            }
            println("Killed! Detecting JVM information...")
            val classPath = ArrayList<String>((vm.findByName("java.property.java.class.path").value as String).split(":"))
            val workingDir = File((vm.findByName("java.property.user.dir").value as String))
            val javaHome = File((vm.findByName("java.property.java.home").value as String))
            println("Minecraft ClassPath: $classPath")
            println("Minecraft JavaHome: $javaHome")
            println("Minecraft PWD: $workingDir")
            println("Finding injection target...")
            var injectableTarget: File? = null
            for (s in classPath) {
                var file: File
                if (s.startsWith("/")) {
                    file = File(s)
                } else {
                    file = File(workingDir, s)
                }
                if (hasGson(file)) {
                    println("Injectable ClassPath Detected: ${file.absolutePath}")
                    injectableTarget = file
                    break
                }
            }
            if (injectableTarget == null) {
                JOptionPane.showConfirmDialog(null, "Could not find an injectable target!\n" +
                        "If you want to submit a bug report, please include these following information:\n" +
                        " - Client Name\n" +
                        " - Injector Version\n",
                    "SpookySky Injector", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE)
                exitProcess(-1)
            }
            println("Injecting...")
            val tempJar = File(System.getProperty("java.io.tmpdir"), "/injection-" + UUID.randomUUID() + ".jar")
            val zipInputStream = ZipInputStream(FileInputStream(injectableTarget))
            val zipOutputStream = ZipOutputStream(FileOutputStream(tempJar))
            var entry = zipInputStream.nextEntry
            while (entry != null) {
                zipOutputStream.putNextEntry(ZipEntry(entry.name))
                if (entry.name == "com/google/gson/Gson.class") {
                    var classNode = ClassNode()
                    var reader = ClassReader(zipInputStream.readBytes())
                    reader.accept(classNode, 0)
                    val targetMethod = classNode.methods.filter { node -> node.name == "<clinit>" }.first()
                    println("Found injection method! Injecting SpookySky into it...")
                    var started = false
                    var newList = InsnList()

                    for (instruction in targetMethod.instructions) {
                        if (instruction is LdcInsnNode) {
                            if (instruction.cst == gsonInjectionStart) {
                                started = true
                                println("Previous SpookySky that's injected into SpookySky has been detected! Removing...")
                            } else if (instruction.cst == gsonInjectionEnd) {
                                started = false
                            } else if (started) {} else {
                                newList.add(instruction)
                            }
                        }
                    }
                    println("Successfully injected SpookySky to target method!")
                    var writer = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                }
                zipOutputStream.closeEntry()
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            zipInputStream.close()
            zipOutputStream.close()
            println("Previous ")
        }

    }


    private fun hasGson(file: File): Boolean {
        val zipStream = ZipInputStream(FileInputStream(file))
        var entry = zipStream.nextEntry
        while (entry != null) {
            if (entry.name.startsWith("com/google/gson")) {
                zipStream.close()
                return true
            }
            zipStream.closeEntry()
            entry = zipStream.nextEntry
        }
        zipStream.close()
        return false
    }

}

