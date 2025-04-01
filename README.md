![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/AntonButov/dagger-dsl?utm_source=oss&utm_medium=github&utm_campaign=AntonButov%2Fdagger-dsl&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# Dagger DSL

KSP processor generated dagger structured files.


```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
}
```
```kotlin
dependencies {
    ksp("com.github.antonbutov:dagger-dsl-processor:<$last-version>")
}
```
