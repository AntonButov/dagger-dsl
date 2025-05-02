![CodeRabbit Pull Request Reviews](https://img.shields.io/coderabbit/prs/github/AntonButov/dagger-dsl?utm_source=oss&utm_medium=github&utm_campaign=AntonButov%2Fdagger-dsl&labelColor=171717&color=FF570A&link=https%3A%2F%2Fcoderabbit.ai&label=CodeRabbit+Reviews)

# Dagger DSL

KSP processor that generates Dagger structured files.

📚 [Documentation](https://antonbutov.github.io/dagger-dsl/)
```kotlin
repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}
```
```kotlin
plugins {
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    id ("kotlin-kapt")
}
```
```kotlin
dependencies {
    ksp("com.github.antonbutov:dagger-dsl-processor:<$last-version>")
    implementation("com.github.antonbutov:dagger-dsl-processor:<$last-version>")
    implementation("com.google.dagger:dagger:2.x")
    kapt("com.google.dagger:dagger-compiler:2.x")
}
```


## 🤝 Contributing
Thanks for checking out Dagger DSL! Contributions of all kinds are welcome — whether it’s code, ideas, docs, or just feedback.

Quick start:
- Fork & clone the repo

- Create a branch: git checkout -b feature/your-feature

- Make your changes

- Run ./gradlew build to test

- Open a Pull Request — I’ll review it asap (with a little help from CodeRabbit 🐰)

Feel free to open an Issue if you’re not sure where to start.
Let’s make this DSL awesome together!