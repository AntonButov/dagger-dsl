import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.vanniktech) apply true
}

group = "io.github.antonbutov"
version = libs.versions.version.get()

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(libs.kotest.runner)
    testImplementation(libs.dagger)
}

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
        name.set("Dagger DSL core.")
        description.set("Dagger DSL.")
        url.set("https://github.com/AntonButov/dagger-dsl-core")
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
            connection.set("scm:git:git://github.com/AntonButov/dagger-dsl-core.git")
            developerConnection.set("scm:git:ssh://git@github.com:AntonButov/dagger-dsl-core.git")
            url.set("https://github.com/AntonButov/dagger-dsl-core")
        }
    }
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
