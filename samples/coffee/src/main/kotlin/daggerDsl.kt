import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.componentSingleton

interface CoffeeShop {
    fun maker(): CoffeeMaker

    fun logger(): CoffeeLogger
}

@DaggerDsl
fun daggerDsl() {
    componentSingleton<CoffeeShop> {
        moduleAbstract {
            bind<Pump, Thermosiphon>()
        }
        moduleAbstract {
            bindSingleton<Heater, ElectricHeater>()
        }
    }
}
