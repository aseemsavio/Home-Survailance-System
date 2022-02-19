plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "asavio.hss.backend"
version = "0.1"

repositories {
    mavenCentral()
}

application {
    mainClass.set("asavio.hss.backend.AppKt")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0")
}
