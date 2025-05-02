plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    kotlin("kapt")
}

group = "com.dagger.dsl"
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

    implementation(kotlin("stdlib"))

    testImplementation(libs.kotest.runner)
}

tasks.test {
    useJUnitPlatform()
}
