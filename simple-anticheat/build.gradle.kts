plugins {
    java
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

tasks.jar {
    manifest {
        attributes["Premain-Class"] = "ml.rektsky.spookysky.ac.AgentMain"
        attributes["Agent-Class"] = "ml.rektsky.spookysky.ac.AgentMain"
        attributes["Can-Redefine-Classes"] = "true"
        attributes["Can-Retransform-Classes"] = "true"
    }
}