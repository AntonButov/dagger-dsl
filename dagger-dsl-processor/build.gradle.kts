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

    testImplementation(libs.kotest.runner)
    api(libs.compilation) // todo https://github.com/AntonButov/dagger-dsl/issues/8
}

tasks.test {
    dependsOn(":dagger-dsl-core:build")
}

tasks.test {
    useJUnitPlatform()
}
