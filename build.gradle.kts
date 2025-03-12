plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ktlint) apply true
}
subprojects {

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.matching { it.name == "test" || it.name == "build" }.configureEach {
        dependsOn("ktlintFormat")
    }

    ktlint {
        debug.set(false)
        android.set(false)
        ignoreFailures.set(true)
        reporters {
            reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        }
    }
}