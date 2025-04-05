plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}

group = "com.dagger.dsl"
version = "1.0-SNAPSHOT"

dependencies {
    api(project(":dagger-dsl-core"))
    ksp(libs.code.factory)
    implementation(libs.ksp)
    implementation(libs.compile.embedded)
    implementation(kotlin("stdlib"))
    implementation(libs.kotlin.poet)
    implementation(libs.kotlin.poet.ksp)
    implementation(libs.dagger)
    annotationProcessor(libs.dagger.compiler)
    implementation(libs.koin)
    implementation(libs.mockk)
    implementation(libs.bundles.ktor)

    testImplementation(libs.bundles.ktor.test)
    testImplementation(libs.kotest.runner)
    api(libs.compilation) // todo rework to testImplementation
}

tasks.test {
    useJUnitPlatform()
}
