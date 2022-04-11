package ml.rektsky.spookysky.buildSrc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.kotlin.dsl.*
import java.net.URI

object SpookySkyOptions {
    const val version = "1.0-SNAPSHOT"
    const val group = "ml.rektsky"
}

class SpookySkyCommon: Plugin<Project> {
    override fun apply(target: Project) {
        target.version = SpookySkyOptions.version
        target.group = SpookySkyOptions.group
        target.repositories {
            mavenLocal()
            rektskyRepository()
            mavenCentral()
        }
    }
}


fun RepositoryHandler.rektskyRepository() {
    maven {
        name = "rektsky-private"
        url = URI("https://maven.pkg.github.com/RektSky/Maven-Repository")
        credentials {
            username = System.getenv("GITHUB_USERNAME")
            password = System.getenv("GITHUB_PASSWORD")
        }
    }
}