import io.gitlab.arturbosch.detekt.Detekt

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.detekt)
}

group = "co.hondaya"
version = "0.0.1"

allprojects {
    repositories {
        mavenCentral()
    }

    apply(plugin = rootProject.libs.plugins.detekt.get().pluginId)

    dependencies {
        detektPlugins(rootProject.libs.detekt.formatting)
    }

    detekt {
        buildUponDefaultConfig = true
        parallel = true
        config.setFrom(files("${rootDir}/config/detekt/detekt.yml"))
        autoCorrect = true
    }

    tasks.withType<Detekt>().configureEach {
        group = "detekt"
        exclude("**/generated/**",)
    }
}

subprojects {
}
