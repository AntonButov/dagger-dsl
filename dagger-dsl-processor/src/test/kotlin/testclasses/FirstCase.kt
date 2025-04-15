package testclasses

import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component
import testcomponents.SimpleComponent

@DaggerDsl
fun di() {
    component<SimpleComponent> {
    }
}
