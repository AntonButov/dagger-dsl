import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import dagger.dsl.core.Component

interface ComponentToFileSpecMapper {
    /**
 * Maps a Dagger component into a Kotlin interface specification.
 *
 * This function transforms the given [Component] into a [TypeSpec] for a Kotlin interface that
 * is named after the component and annotated as a Dagger component. It facilitates the integration
 * of Dagger components with KotlinPoet-based code generation.
 *
 * @param component the Dagger component to convert.
 * @return a [TypeSpec] representing the generated Kotlin interface.
 */
fun map(component: Component): TypeSpec
}

class ComponentToFileSpecMapperImpl : ComponentToFileSpecMapper {
    /**
     * Maps a Dagger component to a Kotlin interface specification.
     *
     * This method creates a [TypeSpec] representing an interface whose name is derived from the provided
     * [component]'s name and annotates it with the Dagger [Component] annotation.
     *
     * @param component the Dagger component used to generate the interface specification.
     * @return a [TypeSpec] representing the generated interface.
     */
    override fun map(component: Component): TypeSpec {
        val generatedClass =
            TypeSpec.interfaceBuilder(component.name)
                .addAnnotation(ClassName(dagger.Component::class.java.packageName, dagger.Component::class.java.simpleName))
                .build()
        return generatedClass
    }
}
