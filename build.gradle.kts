plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "co.hondaya"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }
}

dependencies {
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
