import ml.rektsky.spookysky.buildSrc.*

plugins {
    kotlin("multiplatform") version "1.6.20"
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
                implementation("io.github.kasukusakura:jvm-self-attach:+")
            }
        }
    }
}