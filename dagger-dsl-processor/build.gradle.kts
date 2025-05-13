import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    jacoco
    alias(libs.plugins.vanniktech) apply true
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }

    dependsOn(tasks.test)
}

group = "io.github.antonbutov"
version = libs.versions.core.get()

mavenPublishing {
    coordinates(
        groupId = project.group.toString(),
        artifactId = project.name,
        version = project.version.toString(),
    )

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    if (project.findProperty("signing") == "true") {
        signAllPublications()
    }

    pom {
        name.set("Dagger DSL processor.")
        description.set("Dagger DSL.")
        url.set("https://github.com/AntonButov/dagger-dsl")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("antonbutov")
                name.set("Anton Butov")
                email.set("butov6101@gmail.com")
            }
        }
        scm {
            connection.set("scm:git:git://github.com/AntonButov/dagger-dsl")
            developerConnection.set("scm:git:ssh://github.com/AntonButov/dagger-dsl")
            url.set("https://github.com/AntonButov/dagger-dsl")
        }
    }
}

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

tasks.test {
    dependsOn(":dagger-dsl-core:build")
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
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
