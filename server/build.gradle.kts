import io.ktor.plugin.KtorGradlePlugin

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

repositories {
    mavenCentral()
}

application {
    // Use Ktor EngineMain so application.yaml is loaded automatically
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":infrastructure"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.swagger.code.generator)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}


val dockerComposeFilePath = "./docker-compose.yaml"
tasks.register<Exec>("dockerComposeUp") {
    group = "docker"
    workingDir = rootProject.projectDir
    commandLine("sh", "-c", "docker compose -f $dockerComposeFilePath up -d")
}

tasks.register<Exec>("dockerComposeDown") {
    group = "docker"
    workingDir = rootProject.projectDir
    commandLine("sh", "-c", "docker compose -f $dockerComposeFilePath down --rmi local --remove-orphans --volumes")
}

tasks.named("run") {
    dependsOn("dockerComposeUp")
}
