rootProject.name = "dagger-dsl"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

include("dagger-dsl-processor")
include("samples:coffee")
include("dagger-dsl-core")
include("integration-tests")
