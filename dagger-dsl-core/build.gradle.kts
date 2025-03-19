plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.dagger.dsl"
version = "unspecified"

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(libs.kotest.runner)
}

tasks.test {
    useJUnitPlatform()
}
