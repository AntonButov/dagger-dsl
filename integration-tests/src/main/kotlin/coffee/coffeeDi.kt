package coffee

import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component

@DaggerDsl
fun coffeeDi() =
    component {
        name = "coffeeAppDi"
    }
