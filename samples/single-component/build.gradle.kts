plugins {
    alias(libs.plugins.kotlin.jvm)
}

group = "com.dagger.dsl"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":dagger-dsl-core"))
    implementation(kotlin("stdlib"))
}

tasks.test {
    useJUnitPlatform()
}
