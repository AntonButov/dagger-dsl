package coffee

import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component

// https://github.com/AntonButov/dagger-dsl/issues/28
@DaggerDsl
fun coffeeDi() =
    component<CoffeeShop> {
    }

interface CoffeeShop
