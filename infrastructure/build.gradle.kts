plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.ktor.server.core)
    api(libs.lettuce.core)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
