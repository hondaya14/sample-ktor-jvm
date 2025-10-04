plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.bundles.ktor.server)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}