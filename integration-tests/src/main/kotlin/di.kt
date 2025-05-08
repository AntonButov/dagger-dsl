import dagger.dsl.core.DaggerDsl
import dagger.dsl.core.component
import dagger.dsl.core.get

interface Component {
    fun getVeryImportedImplementation(): VeryImportedImplementation
}

@DaggerDsl
val componentWithProvides =
    component<Component> {
        module {
            provides<VeryImportedImplementation> {
                VeryImportedImplementation(get<Param>())
            }
        }
    }
