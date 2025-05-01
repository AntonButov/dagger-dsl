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
## How it works
You write
```
@DaggerDsl
fun anyNameFunction() =
    componentSingleton<CoffeeShop> {
        moduleAbstract {
            bind<Pump, Thermosiphon>()
        }
        moduleAbstract {
            bindSingleton<Heater, ElectricHeater>()
        }
    }
```
The processor generates:
```
@Component(modules = [ModulePump::class, ModuleHeater::class])
@Singleton
public interface CoffeeShopDsl {
    public fun maker(): CoffeeMaker

    public fun logger(): CoffeeLogger
}

@Module
public abstract class ModuleHeater {
    @Binds
    @Singleton
    public abstract fun bindHeater(`impl`: ElectricHeater): Heater
}

@Module
public abstract class ModulePump {
    @Binds
    public abstract fun bindPump(`impl`: Thermosiphon): Pump
}
```
Dagger takes these files and works.

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
