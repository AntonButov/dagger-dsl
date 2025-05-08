import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.componentSingleton

interface CoffeeShop {
    fun maker(): CoffeeMaker

    fun logger(): CoffeeLogger
}

@DaggerDsl
val daggerDsl =
    componentSingleton<CoffeeShop> {
        bind<Pump, Thermosiphon>()
        bindSingleton<Heater, ElectricHeater>()
    }
