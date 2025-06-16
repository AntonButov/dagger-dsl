## What is Dagger DSL?

Dagger DSL is a Kotlin Symbol Processing (KSP) tool that simplifies dependency injection with Dagger by providing a clean, concise Domain-Specific Language (DSL). It generates the necessary Dagger boilerplate code from your high-level DSL declarations, making dependency injection setup more intuitive and less error-prone.

![](./images/simple-case.png)

## How It Works

Dagger DSL uses KSP (Kotlin Symbol Processing) to analyze your DSL code at compile time and generate the appropriate Dagger components, modules, and bindings. This means:

1. You write clean, concise DSL code
2. The processor generates standard Dagger code
3. Dagger processes the generated code as usual

You write
```kotlin
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
```kotlin
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

This approach gives you the best of both worlds: the simplicity of a DSL with the power and performance of compile-time dependency injection.

Provides
```kotlin
@DaggerDsl
val component = component {
        module {
            provide<Coffee> {
                Coffee(get<Logger>())
            }
        }
    }        
```

## Important Note

Remember to add `@Inject` annotation to your class constructors. Dagger requires this annotation to properly inject dependencies. For example:

```kotlin
class Thermosiphon @Inject constructor(private val heater: Heater) : Pump {
    // Implementation
}

class ElectricHeater @Inject constructor() : Heater {
    // Implementation
}
```

Without the `@Inject` annotation on constructors, Dagger won't be able to create instances of your classes automatically.

Simple case. We can generate modules.
```kotlin
@DaggerDsl
val mainComponent = componentSingleton<CoffeeShop> {
        bind<Pump, Thermosiphon>()      
        bindSingleton<Heater, ElectricHeater>()
    }
```

## Not implemented yet

Multimodule
```kotlin
@DaggerDsl
val secondComponent = component<CoffeeShop>(dependOn = mainComponent) {
            bind<Coffee, Espresso>()
    }
```
Subcomponent
```kotlin
@DaggerDsl
val component = componentSingleton<CoffeeShop> {
        moduleAbstract {
            bind<Pump, Thermosiphon>()
            
            subComponent<SubComponent>() { 
                bind<Coffee, Espresso>()
            }
        }
    }
```
Component DSL
```kotlin
@DaggerDsl
val component = component {
    get<Logger>()
    inject<Fragment>()
}
```
BuildInstance
```kotlin
@DaggerDsl
val component = component {
    buildInstance<Context>()
    get<Logger>()
    inject<Fragment>()
}
```
Multibinding intoSet
```kotlin
@DaggerDsl
val component = component {
    provideIntoSet<EventListener> {
        CustomEventListener()
    }
}
```
Multibinding intoMap
```kotlin
@DaggerDsl
val component = component {
    provideIntoMapStringKey("Key") {
        JsonFormatter()
    }
}
```
Integration tests
```kotlin
@DaggerDsl
val testComponent = component {
    bind<Logger, TestLogger>()
}
```