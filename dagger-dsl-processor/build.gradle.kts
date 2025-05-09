plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    jacoco
}

group = "com.dagger.dsl"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":dagger-dsl-core"))
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

tasks.jacocoTestReport {
    reports {
        csv.required.set(true)
    }
}

tasks.test {
    dependsOn(":dagger-dsl-core:build")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}
