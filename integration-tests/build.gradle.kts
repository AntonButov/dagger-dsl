plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "gradle-tester"
version = "unspecified"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(project(":dagger-dsl-processor"))
    testImplementation(libs.kotest.runner)
    testImplementation(libs.gradle.tester)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
