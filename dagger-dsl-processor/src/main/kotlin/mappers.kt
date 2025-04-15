import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec
import dagger.dsl.core.Component

interface ComponentToFileSpecMapper {
    fun map(component: Component): TypeSpec
}

class ComponentToFileSpecMapperImpl : ComponentToFileSpecMapper {
    override fun map(component: Component): TypeSpec {
        val generatedClass =
            TypeSpec.interfaceBuilder(component.name)
                .addAnnotation(
                    ClassName(
                        dagger.Component::class.java.packageName,
                        dagger.Component::class.java.simpleName,
                    ),
                )
                .build()
        return generatedClass
    }
}
