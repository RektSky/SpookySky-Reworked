package ml.rektsky.spookysky.utils

import com.sun.tools.attach.VirtualMachine
import io.github.karlatemp.unsafeaccessor.Unsafe
import io.github.karlatemp.unsafeaccessor.UnsafeAccess
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.instrument.Instrumentation
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.management.ManagementFactory
import java.lang.management.RuntimeMXBean
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.net.URLClassLoader
import java.util.*
import java.util.jar.Manifest
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.collections.ArrayList


object CustomJvmSelfAttach {

    var instrumentation0: Instrumentation? = null
    val EXTERNAL_AGENT_BYTECODE: ByteArray = Base64.getDecoder().decode(
        "yv66vgAAADQAGgEAKGlvL2dpdGh1Yi9rYXN1a3VzYWt1cmEvanNhL0V4dGVybmFsQWdlbnQHAAEBABBqYXZhL2xhbmcvT2JqZWN0BwADAQASRXh0ZXJuYWxBZ2VudC5qYXZhAQAPaW5zdHJ1bWVudGF0aW9uAQAmTGphdmEvbGFuZy9pbnN0cnVtZW50L0luc3RydW1lbnRhdGlvbjsBAAY8aW5pdD4BAAMoKVYMAAgACQoABAAKAQAEdGhpcwEAKkxpby9naXRodWIva2FzdWt1c2FrdXJhL2pzYS9FeHRlcm5hbEFnZW50OwEAB3ByZW1haW4BADsoTGphdmEvbGFuZy9TdHJpbmc7TGphdmEvbGFuZy9pbnN0cnVtZW50L0luc3RydW1lbnRhdGlvbjspVgwABgAHCQACABABAANvcHQBABJMamF2YS9sYW5nL1N0cmluZzsBAANpbnMBAAlhZ2VudG1haW4BAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAKU291cmNlRmlsZQAhAAIABAAAAAEACQAGAAcAAAADAAEACAAJAAEAFgAAAC8AAQABAAAABSq3AAuxAAAAAgAXAAAABgABAAAABQAYAAAADAABAAAABQAMAA0AAAAJAA4ADwABABYAAAA9AAEAAgAAAAUrswARsQAAAAIAFwAAAAoAAgAAAAkABAAKABgAAAAWAAIAAAAFABIAEwAAAAAABQAUAAcAAQAJABUADwABABYAAAA9AAEAAgAAAAUrswARsQAAAAIAFwAAAAoAAgAAAA0ABAAOABgAAAAWAAIAAAAFABIAEwAAAAAABQAUAAcAAQABABkAAAACAAU="
    )
    val UA = UnsafeAccess.getInstance()

    @Synchronized
    fun init(tmp: File) {
        if (instrumentation0 != null) {
            return
        }
        try {
            init0(tmp)
        } catch (throwable: Throwable) {
            throw ExceptionInInitializerError(throwable)
        }
    }

    fun getInstrumentation(): Instrumentation {
        return instrumentation0
            ?: throw NullPointerException(
                "Instrumentation not initialized, call `CustomJvmSelfAttach.init` first"
            )
    }

    @Throws(Throwable::class)
    private fun genEAFile(dir: File, cnname: String): File {
        var rdfile: File
        do {
            rdfile = File(dir, "external-agent-" + UUID.randomUUID().toString() + ".jar")
        } while (rdfile.exists())
        ZipOutputStream(
            BufferedOutputStream(
                FileOutputStream(rdfile)
            )
        ).use { zos ->
            zos.putNextEntry(ZipEntry("META-INF/MANIFEST.MF"))
            val mf = Manifest()
            mf.getMainAttributes().putValue("Manifest-Version", "1")
            mf.getMainAttributes().putValue("Premain-Class", cnname)
            mf.getMainAttributes().putValue("Agent-Class", cnname)
            mf.getMainAttributes().putValue("Launcher-Agent-Class", cnname)
            mf.getMainAttributes().putValue("Can-Redefine-Classes", "true")
            mf.getMainAttributes().putValue("Can-Retransform-Classes", "true")
            mf.getMainAttributes().putValue("Can-Set-Native-Method-Prefix", "true")
            mf.write(zos)
        }
        return rdfile
    }

    @Throws(Throwable::class)
    private fun init0(tmp: File) {
        var cnname = "io.github.kasukusakura.jsa.p.AgentA"
        cnname = cnname.replace('-', '_')

        // Anti relocate
        val EAN = StringBuilder()
            .append("io").append(".git").append("hub.kasuku").append("sakura.jsa.")
            .append("ExternalAgent")
            .toString()
        tmp.mkdirs()
        run {
            val listFiles: Array<File> = tmp.listFiles()
            val sysNow = System.currentTimeMillis()
            if (listFiles != null) for (subFile in listFiles) {
                if (subFile.isFile() && subFile.getName().startsWith("external-agent-")) {
                    try {
                        if (sysNow - subFile.lastModified() > 1000L * 60) {
                            subFile.delete()
                        }
                    } catch (ignored: Throwable) {
                    }
                }
            }
        }
        val rdfile: File = genEAFile(tmp, cnname)
        run {
            var code = EXTERNAL_AGENT_BYTECODE
            code = BytecodeUtil.replace(
                code,
                EAN,
                cnname
            )
            code = BytecodeUtil.replace(
                code,
                EAN.replace('.', '/'),
                cnname.replace('.', '/')
            )
            code = BytecodeUtil.replace(
                code,
                "L" + EAN.replace('.', '/') + ";",
                "L" + cnname.replace('.', '/') + ";"
            )
            UA.unsafe.defineClass(null, code, 0, code.size, ClassLoader.getSystemClassLoader(), null)
        }
        loadAgent(cnname, EAN, rdfile)
        fetchInst(cnname)
    }

    @Throws(Throwable::class)
    private fun loadAgent(cnname: String, EAN: String, rdfile: File) {
        val absp: String = rdfile.getAbsolutePath()
        val allFails: MutableList<Throwable> = ArrayList()
        try { //java.instrument
            val met: Method = Class.forName("sun.instrument.InstrumentationImpl")
                .getDeclaredMethod("loadAgent", String::class.java)
            UA.setAccessible<AccessibleObject>(met, true)
            met.invoke(null, absp)
            return
        } catch (e: Throwable) {
            allFails.add(e)
        }
        var pid: Long
        var pid_str: String
        try {
            val PH = Class.forName("java.lang.ProcessHandle")
            val lk = MethodHandles.lookup()
            val currentProcess = lk.findStatic(PH, "current", MethodType.methodType(PH)).invoke()
            pid = lk.findVirtual(PH, "pid", MethodType.methodType(Long::class.javaPrimitiveType))
                .invoke(currentProcess) as Long
            pid_str = java.lang.Long.toString(pid)
        } catch (ignored: Throwable) {
            val runtimeMXBean: RuntimeMXBean = ManagementFactory.getRuntimeMXBean()
            val name: String = runtimeMXBean.getName()
            pid_str = name.substring(0, name.indexOf('@'))
            pid = pid_str.toLong()
        }
        try { // jdk.attach
            System.setProperty("jdk.attach.allowAttachSelf", "true")
            try {
                val VM = Class.forName("jdk.internal.misc.VM")
                val f: Field = VM.getDeclaredField("savedProps")
                UA.setAccessible<AccessibleObject>(f, true)
                f["jdk.attach.allowAttachSelf"] = "true"
            } catch (a: Throwable) {
                allFails.add(a)
            }
            try {
                val HotSpotVirtualMachine = Class.forName("sun.tools.attach.HotSpotVirtualMachine")
                UA.unsafe.ensureClassInitialized(HotSpotVirtualMachine)
                val attach_self: Field = HotSpotVirtualMachine.getDeclaredField("ALLOW_ATTACH_SELF")
                UA.setAccessible<AccessibleObject>(attach_self, true)
                try {
                    attach_self.setBoolean(null, true)
                } catch (w: Throwable) {
                    allFails.add(w)
                    val unsafe: Unsafe = UA.unsafe
                    unsafe.putBoolean(
                        unsafe.staticFieldBase(attach_self),
                        unsafe.staticFieldOffset(attach_self),
                        true
                    )
                }
            } catch (a: Throwable) {
                allFails.add(a)
            }
            val attach: VirtualMachine = VirtualMachine.attach(pid_str)
            attach.loadAgent(absp)
            attach.detach()
            return
        } catch (e: Throwable) {
            allFails.add(e)
        }
        try {
            var javaHome = File(System.getProperty("java.home"))
            if (javaHome.getName().equals("jre")) {
                javaHome = javaHome.getParentFile()
            }
            val tools = File(javaHome, "lib/tools.jar")
            if (tools.exists()) {
                val urlClassLoader = URLClassLoader(
                    arrayOf(
                        tools.toURI().toURL()
                    )
                )
                val vm = urlClassLoader.loadClass("com.sun.tools.attach.VirtualMachine")
                val attach = vm.getMethod("attach", String::class.java).invoke(null, pid_str)
                vm.getMethod("loadAgent", String::class.java).invoke(attach, absp)
                vm.getMethod("detach").invoke(attach)
                return
            }
        } catch (e: Throwable) {
            allFails.add(e)
        }
        val error = LinkageError("Failed to attach self, try upgrade your java version or use JDK")
        for (t in allFails) {
            error.addSuppressed(t)
        }
        throw error
    }

    @Throws(Throwable::class)
    private fun fetchInst(cname: String) {
        val c = Class.forName(cname, false, ClassLoader.getSystemClassLoader())
        val f: Field = c.getField("instrumentation")
        UA.setAccessible<AccessibleObject>(f, true)
        val instrumentation = f.get(null) as Instrumentation
        f.set(null, null)
        CustomJvmSelfAttach.instrumentation0 = instrumentation
        // System.out.println(instrumentation);
    }

    @JvmStatic
    fun main(args: Array<String>) {
        CustomJvmSelfAttach.init(File("build/jsa"))
        println(instrumentation0)
    }
    
}