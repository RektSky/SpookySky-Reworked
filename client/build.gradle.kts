import org.jetbrains.kotlin.gradle.targets.js.webpack.*
import ml.rektsky.spookysky.buildSrc.*
import java.net.*

val ktorVersion = "2.0.0"
val production = true

plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}


apply<SpookySkyCommon>()


repositories {
    mavenCentral()
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

                // Ktor
            }
        }
        val jsMain by getting {
            dependencies {

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

tasks {
    val shadowCreate by creating(com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class) {
        archiveClassifier.set("all")
        val main by kotlin.jvm().compilations
        from(main.output)
        configurations += main.compileDependencyFiles as Configuration
        exclude("com/google/gson/**")
        val taskName = if (production) "jsBrowserProductionWebpack" else "jsBrowserDevelopmentWebpack"
        val webpackTask = project.tasks.getByName<KotlinWebpack>(taskName)
        dependsOn(webpackTask)
        from(File(webpackTask.destinationDirectory, webpackTask.outputFileName))
//        configurations += main.runtimeDependencyFiles as Configuration
    }
    val build by existing {
        dependsOn(shadowCreate)
    }
}

