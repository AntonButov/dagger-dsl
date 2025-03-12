import org.gradle.kotlin.dsl.provideDelegate
import java.net.URI
import java.nio.file.Files
import java.nio.file.Paths

plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.dagger.dsl"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":dagger-dsl-core"))
    implementation(libs.ksp)
    implementation(libs.compile.embedded)
    implementation(kotlin("stdlib"))

    testImplementation(libs.kotest.runner)
    api(libs.compilation) // todo rework to testImplementation
}

tasks.test {
    useJUnitPlatform()
}

val kotlinVersion = libs.versions.kotlin.get()

val downloadKotlinStdlib by tasks.registering {
    val outputDir = layout.buildDirectory.dir("libs").get().asFile
    val outputFile = File(outputDir, "kotlin-stdlib-$kotlinVersion.jar")
    val artifactUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$kotlinVersion/kotlin-stdlib-$kotlinVersion.jar"

    outputs.file(outputFile)

    onlyIf { !outputFile.exists() }

    doLast {
        outputDir.mkdirs()
        println("Downloading kotlin-stdlib-$kotlinVersion.jar...")
        URI(artifactUrl).toURL().openStream().use { input ->
            Files.copy(input, Paths.get(outputFile.toURI()))
        }
        println("Download complete: ${outputFile.absolutePath}")
    }
}

tasks.named("compileKotlin") {
    dependsOn(downloadKotlinStdlib)
}
