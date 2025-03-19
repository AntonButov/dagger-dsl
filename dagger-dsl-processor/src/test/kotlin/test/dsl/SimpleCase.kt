package test.dsl

import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component

@DaggerDsl
fun dsl() =
    component {
        name = "test"
        module {
        }
    }
