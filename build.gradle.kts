plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
    id("jacoco")  // Добавляем плагин JaCoCo вместо Kover
}

// Настройка JaCoCo
jacoco {
    toolVersion = "0.8.8"  // Используйте последнюю стабильную версию
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(true)
    }
}

// Добавляем задачу для проверки покрытия
tasks.test {
    finalizedBy(tasks.jacocoTestReport)
    useJUnitPlatform()
}
