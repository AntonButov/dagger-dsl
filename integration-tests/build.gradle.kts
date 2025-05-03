plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    id("kotlin-kapt")
}

group = "gradle-tester"
version = "unspecified"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    ksp(project(":dagger-dsl-processor"))
    implementation(project(":dagger-dsl-core"))
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    testImplementation(libs.kotest.runner)
    testImplementation(libs.gradle.tester)
    testImplementation(libs.mockk)
}

tasks.test {
    useJUnitPlatform()
}
