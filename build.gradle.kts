plugins {
    kotlin("jvm") version "1.9.10"
    id("com.google.cloud.tools.jib") version "3.4.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.kafka:kafka-clients:3.3.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.+")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("MainKt")
}

jib {
    from {
        image = "eclipse-temurin:17-jre-alpine"
    }

    to {
        image = "app"
    }
}