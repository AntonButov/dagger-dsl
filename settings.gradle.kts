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
    }
}

include("dagger-dsl-processor")
include("samples:coffee")
include("samples:simple-case")
include("dagger-dsl-core")
include("integration-tests")
