plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.11.1"
}

group = "com.kagg886"
version = "1.0"

repositories {
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("net.mamoe:mirai-core:2.11.1")
    implementation("org.jsoup:jsoup:1.14.3")
    implementation("org.java-websocket:Java-WebSocket:1.5.3")
    implementation("org.json:json:20220320")
}
