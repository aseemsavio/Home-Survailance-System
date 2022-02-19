plugins {
    application
    id("org.jetbrains.kotlin.jvm") version "1.6.20-M1"
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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0-native-mt")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.20-M1")
    implementation("org.apache.kafka:kafka-clients:2.8.0")
}
