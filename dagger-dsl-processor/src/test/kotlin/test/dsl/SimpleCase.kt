package test.dsl

import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component

@DaggerDsl
fun dsl() =
    // todo for integration tests
    component<CoffeeShop> {
    }

interface CoffeeShop {
    fun maker(): String
}
