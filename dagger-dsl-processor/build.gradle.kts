plugins {
    kotlin("jvm")
}

group = "com.dagger.dsl"
version = "1.0-SNAPSHOT"

dependencies {
    //testImplementation(platform("org.junit:junit-bom:5.10.0"))
    //testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(libs.kotest.runner)
    testImplementation(libs.compilation)
}

tasks.test {
    useJUnitPlatform()
}