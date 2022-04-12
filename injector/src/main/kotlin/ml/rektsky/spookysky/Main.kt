package ml.rektsky.spookysky

import ml.rektsky.spookysky.asm.CustomClassWriter
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*
import sun.jvmstat.monitor.HostIdentifier
import sun.jvmstat.monitor.MonitoredHost
import sun.jvmstat.monitor.MonitoredVmUtil
import sun.jvmstat.monitor.VmIdentifier
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.swing.JOptionPane
import kotlin.system.exitProcess


object Main {

    const val gsonInjectionStart = "de8dc0c0-58bc-4ebb-95bc-95cb44b90ce9"
    const val gsonInjectionEnd = "3e9e7328-6202-431e-8ad5-e2e1dbf5b0c7"

    const val text = "Thanks for using SpookySky! You can start Minecraft now.\n" +
            "After pressing OK, It will restart your minecraft in order to do screen-sharing bypass\n" +
            "If anything broke after using it, please delete the .minecraft/libraries directory, or\n" +
            "if you are running lunar client: <user home>/.lunarclient/1.8/offline/lunar-libs.jar.\n" +
            "If it still doesn't work, please contact the Developer of SpookySky!\n" +
            "\n" +
            "Status:\n" +
            "[WORKING ] Screensharing Bypass\n" +
            "[WORKING ] Linux Support\n" +
            "[UPDATING] Windows Support\n" +
            "\n" +
            "\n" +
            "WARNING: Currently, it only works on Linux\n" +
            "WARNING: Don't close it until the minecraft has been launched.\n" +
            "WARNING: It's gonna use port 6930 and 6931 as communication port, and 8040 as Web GUI port."

    val windowsFlag = System.getProperty("os.name").lowercase(Locale.ENGLISH).contains("windows")

    @JvmStatic
    fun main(args: Array<String>) {
        var agent: ZipInputStream? = null
        if (args.isEmpty()) {
            for (resource in Main.javaClass.classLoader.getResources("")) {
                if (resource.file.endsWith(".jar")) {
                    agent = ZipInputStream(Main.javaClass.classLoader.getResourceAsStream(resource.file))
                }
            }
        } else {
            agent = ZipInputStream(FileInputStream(args[0]))
        }


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
            println("Command Line: ${MonitoredVmUtil.commandLine(vm)}")
            println("Found Minecraft! Killing it...")
            val cwdRetriever = Runtime.getRuntime().exec("readlink -e /proc/$pid/cwd")
            var workingDir: File? = null
            if (windowsFlag) {
                Runtime.getRuntime().exec("TASKKILL /F /PID $pid").waitFor()
            } else {
                Runtime.getRuntime().exec("kill -9 $pid").waitFor()
                workingDir = File(cwdRetriever.inputStream.readBytes().toString(Charset.defaultCharset()).split("\n")[0])
            }
            workingDir!!
            println("Killed! Detecting JVM information...")
            val classPath = ArrayList<String>((vm.findByName("java.property.java.class.path").value as String).split(":"))
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
                println("Found ClassPath: ${file.absolutePath}")
                try {
                    if (hasGson(file)) {
                        println("Injectable ClassPath Detected: ${file.absolutePath}")
                        injectableTarget = file
                        break
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
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
            val zipInputStream = ZipInputStream(FileInputStream(injectableTarget))
            val byteArrayOutputStream = ByteArrayOutputStream()
            val zipOutputStream = ZipOutputStream(byteArrayOutputStream)
            var entry = agent!!.nextEntry
            while (entry != null) {
                zipOutputStream.putNextEntry(ZipEntry(entry.name))
                zipOutputStream.write(agent.readBytes())
                zipOutputStream.closeEntry()
                agent.closeEntry()
                entry = agent.nextEntry
            }
            agent.close()
            entry = zipInputStream.nextEntry
            while (entry != null) {
                if (!entry.name.startsWith("ml/rektsky/spookysky/")) {
                    try {
                        zipOutputStream.putNextEntry(ZipEntry(entry.name))
                        var output = zipInputStream.readBytes()
                        if (entry.name == "com/google/gson/Gson.class") {
                            var classNode = ClassNode()
                            var reader = ClassReader(output)
                            reader.accept(classNode, 0)
                            val targetMethod = classNode.methods.filter { node -> node.name == "<clinit>" }.first()
                            println("Found injection method! Injecting SpookySky into it...")
                            var started = false
                            var newList = InsnList()
                            newList.add(LdcInsnNode(gsonInjectionStart))
                            newList.add(FieldInsnNode(Opcodes.GETSTATIC, "ml/rektsky/spookysky/Client", "INSTANCE", "Lml/rektsky/spookysky/Client;"))
                            newList.add(InsnNode(Opcodes.POP))
                            newList.add(LdcInsnNode(gsonInjectionEnd))
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
                            targetMethod.instructions = newList
                            println("Successfully injected SpookySky to target method!")
                            var writer = CustomClassWriter()
                            classNode.accept(writer)
                            output = writer.toByteArray()
                        }
                        zipOutputStream.write(output)
                        zipOutputStream.closeEntry()
                    } catch (e: Exception) {}
                }
                zipInputStream.closeEntry()
                entry = zipInputStream.nextEntry
            }
            zipInputStream.close()
            zipOutputStream.close()
            injectableTarget.writeBytes(byteArrayOutputStream.toByteArray())
            println("Successfully injected! Launching Minecraft...")
            val socket = ServerSocket(6931)
            val builder = ProcessBuilder()
            builder.command(File(javaHome, "bin/java").absolutePath,
                *jvmArgs.replace("-XX:+DisableAttachMechanism", "").split(" ").toTypedArray(),
                "-cp", classPath.joinToString(":"),
                MonitoredVmUtil.mainClass(vm, true),
                *mainArgs.split(" ").toTypedArray()
            )
            builder.directory(workingDir)
            for (s in builder.command()) {
                print("$s ")
            }
            print("\n")
            var process = builder.start()
            val thread = Thread {
                val scanner = Scanner(process.inputStream)
                while (scanner.hasNextLine()) {
                    println(scanner.nextLine())
                }
                try {
                    process.waitFor()
                } catch (ignored: InterruptedException) {
                }
                println("Process exited with code " + process.exitValue())
            }
            val threadE = Thread {
                val scanner = Scanner(process.errorStream)
                while (scanner.hasNextLine()) {
                    println(scanner.nextLine())
                }
                try {
                    process.waitFor()
                } catch (ignored: InterruptedException) {
                }
            }
            thread.start()
            threadE.start()
            val startTime = System.currentTimeMillis()
            var thread1 = Thread {
                try {
                    while (true) {
                        if (System.currentTimeMillis() > startTime + 60000) {
                            JOptionPane.showConfirmDialog(
                                null, "Failed to inject!\n" +
                                        "If you can't even launch the game normally, please delete \n" +
                                        "${injectableTarget.absolutePath}\n" +
                                        "If any error shows up after deleting it, please reinstall the Minecraft client.\n" +
                                        "If you want to submit a bug report, please include these following information:\n" +
                                        " - Client Name\n" +
                                        " - Injector Version\n",
                                "SpookySky Injector", JOptionPane.CLOSED_OPTION, JOptionPane.ERROR_MESSAGE
                            )
                            exitProcess(-1)
                        }
                        Thread.sleep(100)
                    }
                } catch (ignored: Exception) {}
            }
            thread1.start()
            socket.accept()
            JOptionPane.showConfirmDialog(null, "Successfully injected into Minecraft!\n" +
                    "If the client doesn't work, then it probably means that it doesn't support the Minecraft " +
                    "client you've launched.",
                "SpookySky Injector", JOptionPane.CLOSED_OPTION, JOptionPane.INFORMATION_MESSAGE)
            socket.close()
            thread1.stop()
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

