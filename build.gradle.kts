import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    val kotlinVersion = "1.7.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.13.0"
}

group = "com.kagg886"
version = "1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("net.mamoe:mirai-core:2.13.0")
    implementation("org.jsoup:jsoup:1.15.3")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("org.json:json:20220924")
}
