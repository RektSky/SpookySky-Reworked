rootProject.name = "spookysky"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}


include("injector")
include("client")
include("client")
include("simple-anticheat")
