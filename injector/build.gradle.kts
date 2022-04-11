import ml.rektsky.spookysky.buildSrc.SpookySkyCommon
import org.gradle.api.internal.file.collections.*

plugins {
    kotlin("jvm") version "1.6.20"
}


apply<SpookySkyCommon>()


repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation(files(System.getProperty("java.home") + "/../lib/tools.jar"))

    implementation("org.ow2.asm:asm-util:9.3")
    implementation("org.ow2.asm:asm:9.3")
}
var clientJar = File(rootProject.project(":client").buildDir, "/libs/client-${project.version}-all.jar")

tasks.jar {
    dependsOn(":client:build")
    from(clientJar)

    manifest {
        attributes["Main-Class"] = "ml.rektsky.spookysky.Main"
    }

}

val runLunar by tasks.register<JavaExec>("runLunar") {
    val lunarHome = File(File(System.getProperty("user.home")), ".lunarclient")
    val jreHome = File(lunarHome, "jre/1.8/").listFiles { file -> file.isDirectory }!!.first()
    this.executable(File(jreHome, "bin/java"))
    this.main = "com.moonsworth.lunar.patcher.LunarMain"
    this.workingDir = File(lunarHome, "offline/1.8")
    this.jvmArgs = listOf(
        "--add-modules", "jdk.naming.dns",
        "--add-exports", "jdk.naming.dns/com.sun.jndi.dns=java.naming",
        "--add-opens", "java.base/java.io=ALL-UNNAMED",
        "-Djna.boot.library.path=natives",
        "-Dlog4j2.formatMsgNoLookups=true",
        "-Djava.library.path=natives",
//        "-XX:+DisableAttachMechanism",
        "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=6950",
    )

    this.classpath = files(this.workingDir.listFiles { _, name -> name?.endsWith(".jar") == true})
    this.args = listOf(
        "--version", "1.8",
        "--accessToken", "0",
        "--assetIndex", "1.8",
        "--userProperties", "{}",
        "--gameDir", File(File(System.getProperty("user.home")), ".minecraft").absolutePath,
        "--texturesDir", File(lunarHome, "textures").absolutePath,
        "--launcherVersion", "2.9.1",
        "--hwid", "28fe925eeca05406329c39b8dbfafdba40117466843c5f874fae191b7080c499",
        "--width", "854",
        "--height", "480",
    )
}


val runInjector by tasks.register<JavaExec>("run") {
    dependsOn("build")
    group = "run configurations"
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("ml.rektsky.spookysky.Main")
    args(clientJar.absolutePath)
}


