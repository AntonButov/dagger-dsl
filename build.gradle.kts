plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktlint) apply true
}
private val ktLintConfig: org.jlleitschuh.gradle.ktlint.KtlintExtension.() -> Unit = {
    debug.set(false)
    android.set(false)
    ignoreFailures.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
    }
}

subprojects {

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    tasks.matching { it.name == "test" || it.name == "build" }.configureEach {
        dependsOn("ktlintFormat")
    }

    ktlint {
        ktLintConfig()
    }
}

ktlint {
    ktLintConfig()
}

tasks.named("build") {
    dependsOn("ktlintFormat")
}

tasks.named("test") {
    dependsOn("ktlintFormat")
}
