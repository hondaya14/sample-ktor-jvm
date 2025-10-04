plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.serialization)
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
    implementation(project(":usecase"))
    implementation(project(":infrastructure"))
    implementation(libs.bundles.ktor.server)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.h2)
    implementation(libs.ktor.server.swagger)
    implementation(libs.ktor.server.openapi)
    implementation(libs.swagger.code.generator)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
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
