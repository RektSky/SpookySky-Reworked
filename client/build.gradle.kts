import org.jetbrains.kotlin.gradle.targets.js.webpack.*
import ml.rektsky.spookysky.buildSrc.*
import java.net.*

val ktorVersion = "2.0.0"
val production = false

plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}


apply<SpookySkyCommon>()


repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    maven {
        url = uri("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    }
}

kotlin {
    /* Targets configuration omitted. 
    *  To find out how to configure the targets, please follow the link:
    *  https://kotlinlang.org/docs/reference/building-mpp-with-gradle.html#setting-up-targets */

    jvm()
    js {
        browser()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
                val encoding = "1.1.0"
                implementation("io.matthewnelson.kotlin-components:encoding-base16:$encoding")
                implementation("io.matthewnelson.kotlin-components:encoding-base32:$encoding")
                implementation("io.matthewnelson.kotlin-components:encoding-base64:$encoding")
                // Ktor
            }
        }
        val jsMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-html-js:0.7.5")
                // Ktor
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("org.ow2.asm:asm-util:9.3")
                implementation("org.ow2.asm:asm:9.3")
                implementation("io.github.karlatemp:unsafe-accessor:1.6.2")
                implementation("org.java-websocket:Java-WebSocket:1.5.3")
                implementation(files(System.getProperty("java.home") + "/../lib/tools.jar"))

                compileOnly("org.apache.logging.log4j:log4j-core:2.17.2")
                compileOnly("com.google.code.gson:gson:2.9.0")


                // Ktor
            }
        }
    }
}

tasks.getByName("jsBrowserProductionWebpack") {
    this.onlyIf {production}
}

tasks {
    val shadowCreate by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        archiveClassifier.set("all")
        val main by kotlin.jvm().compilations
        from(main.output)
        configurations += main.compileDependencyFiles as Configuration
//        exclude("com/google/gson/**")
        val taskName = if (production) "jsBrowserProductionWebpack" else "jsBrowserDevelopmentWebpack"
        val webpackTask = project.tasks.getByName<KotlinWebpack>(taskName)
        dependsOn(webpackTask)
        from(File(webpackTask.destinationDirectory, webpackTask.outputFileName))
    }
    val build by existing {
        dependsOn(shadowCreate)
    }
}


tasks.register("runHttpServer") {
    group = "run"
    dependsOn("build")
    doLast {
        javaexec {
            val lunarHome = File(File(System.getProperty("user.home")), ".lunarclient")
            val jreHome = File(lunarHome, "jre/1.8/").listFiles { file -> file.isDirectory }!!.first()
            this.executable(File(jreHome, "bin/java"))
            this.main = "ml.rektsky.spookysky.ClientKt"
            this.workingDir = File(lunarHome, "offline/1.8")
            this.classpath = files(project.buildDir.absolutePath + "/libs/client-${project.version}-all.jar")
        }
    }
}

