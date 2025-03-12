plugins {
    kotlin("jvm")
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
