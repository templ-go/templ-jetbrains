import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.date
import org.jetbrains.changelog.ChangelogSectionUrlBuilder
import org.jetbrains.changelog.markdownToHTML

fun properties(key: String) = providers.gradleProperty(key)

plugins {
    id("java")
    alias(libs.plugins.kotlin) // Kotlin support
    id("org.jetbrains.intellij.platform") version "2.6.0" // Gradle IntelliJ Plugin
    id("org.jetbrains.intellij.platform.migration") version "2.6.0"
    alias(libs.plugins.changelog) // Gradle Changelog Plugin
    alias(libs.plugins.qodana) // Gradle Qodana Plugin
    alias(libs.plugins.kover) // Gradle Kover Plugin
}

group = properties("pluginGroup").get()
version = properties("pluginVersion").get()

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

// Add Grammar Kit code generated sources to the main source set
sourceSets["main"].java.srcDirs("src/main/gen")


intellijPlatform {
    pluginConfiguration {
        name = properties("pluginName")
        description = providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
            val start = "<!-- Plugin description -->"
            val end = "<!-- Plugin description end -->"

            with (it.lines()) {
                if (!containsAll(listOf(start, end))) {
                    throw GradleException("Plugin description section not found in README.md:\n$start ... $end")
                }
                subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
            }
        }
        val changelog = project.changelog // local variable for configuration cache compatibility
        // Get the latest available change notes from the changelog file
        changeNotes = properties("pluginVersion").map { pluginVersion ->
            with(changelog) {
                renderItem(
                    (getOrNull(pluginVersion) ?: getUnreleased())
                        .withHeader(false)
                        .withEmptySections(false),
                    Changelog.OutputType.HTML,
                )
            }
        }
    }
    signing {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishing {
        token.set(System.getenv("PUBLISH_TOKEN"))
        channels = properties("pluginVersion").map { listOf(it.split('-').getOrElse(1) { "default" }.split('.').first()) }
    }g
}
dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2025.2", useInstaller = true)


        plugins(properties("platformPlugins").map { it.split(',') })
        bundledPlugins(properties("platformBundledPlugins").map { it.split(',') })
    }
}
changelog {
    groups.empty()
    repositoryUrl = properties("pluginRepositoryUrl")
}

tasks {

    wrapper {
        gradleVersion = properties("gradleVersion").get()
    }
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }


}
