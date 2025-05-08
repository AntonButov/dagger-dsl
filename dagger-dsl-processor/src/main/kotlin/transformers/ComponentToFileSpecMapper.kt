package transformers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import models.Component
import models.ComponentTypeMethod
import javax.inject.Singleton

interface ComponentToFileSpecMapper {
    fun map(component: Component): SpecsForWriter
}

class ComponentToFileSpecMapperImpl(
    private val abstractModuleToTypeSpecMapper: AbstractModuleToTypeSpecMapper,
    private val moduleToTypeSpecMapper: ModuleToTypeSpecMapper,
) : ComponentToFileSpecMapper {
    override fun map(component: Component): SpecsForWriter {
        val componentTypeSpec = mapComponentToSpec(component)
        val abstractModuleTypeSpec = abstractModuleToTypeSpecMapper.map(component.abstractModules)
        val moduleTypeSpec = moduleToTypeSpecMapper.map(component.providesModules)
        return SpecsForWriter(
            componentTypeSpec,
            abstractModuleTypeSpec + moduleTypeSpec,
        )
    }

    private fun mapComponentToSpec(component: Component): TypeSpec {
        val originalClass = component.componentType

        val componentClassName =
            ClassName(
                dagger.Component::class.java.packageName,
                dagger.Component::class.java.simpleName,
            )

        val annotationBuilder = AnnotationSpec.builder(componentClassName)

        val moduleNames = (component.providesModules + component.abstractModules).map { it.name }
        if (moduleNames.isNotEmpty()) {
            annotationBuilder.addMember(
                "modules = [%L]",
                moduleNames.joinToString(", ") { "$it::class" },
            )
        }

        val interfaceBuilder =
            TypeSpec.interfaceBuilder(originalClass.name + "Dsl")
                .addAnnotation(annotationBuilder.build())

        if (component.isSingleton) {
            interfaceBuilder.addAnnotation(
                ClassName(
                    Singleton::class.java.packageName,
                    Singleton::class.java.simpleName,
                ),
            )
        }

        originalClass
            .methods
            .forEach { method ->
                interfaceBuilder.addFunction(createFunctionFromMethod(method))
            }
        return interfaceBuilder.build()
    }

    private fun createFunctionFromMethod(method: ComponentTypeMethod): FunSpec {
        val funBuilder =
            FunSpec.builder(method.name)
                .addModifiers(KModifier.ABSTRACT)

        method.params.forEach { param ->
            funBuilder.addParameter(
                param.name,
                ClassName(param.type.packageName, param.type.name),
            )
        }

        method.returnType?.let { returnType ->
            funBuilder.returns(ClassName(returnType.packageName, returnType.name))
        }

        return funBuilder.build()
    }
}

data class SpecsForWriter(
    val componentSpec: TypeSpec,
    val moduleSpec: List<TypeSpec>,
)
