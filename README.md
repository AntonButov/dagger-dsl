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
