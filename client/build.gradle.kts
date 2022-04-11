import ml.rektsky.spookysky.buildSrc.*

plugins {
    kotlin("multiplatform") version "1.6.20"
    id("com.github.johnrengelman.shadow") version "5.1.0"
}


apply<SpookySkyCommon>()


repositories {
    mavenCentral()
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
            }
        }
        val jsMain by getting {

        }
        val jvmMain by getting {
            dependencies {
                implementation("org.ow2.asm:asm-util:9.3")
                implementation("org.ow2.asm:asm:9.3")
                implementation("io.github.karlatemp:unsafe-accessor:1.6.2")
                implementation(files(System.getProperty("java.home") + "/../lib/tools.jar"))
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
        configurations += main.runtimeDependencyFiles as Configuration
    }
    val build by existing {
        dependsOn(shadowCreate)
    }
}
