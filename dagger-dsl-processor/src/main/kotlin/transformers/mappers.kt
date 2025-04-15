package transformers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import models.AbstractModule
import models.Bind
import models.Component
import models.ComponentTypeMethod
import javax.inject.Singleton

interface ComponentToFileSpecMapper {
    fun map(component: Component): SpecsForWriter
}

internal class ComponentToFileSpecMapperImpl : ComponentToFileSpecMapper {
    override fun map(component: Component): SpecsForWriter {
        val componentTypeSpec = mapComponent(component)
        val moduleTypeSpec = mapAbstractModules(component.abstractModules)
        return SpecsForWriter(
            componentTypeSpec,
            moduleTypeSpec,
        )
    }

    private fun mapComponent(component: Component): TypeSpec {
        val originalClass = component.componentType

        val componentClassName =
            ClassName(
                dagger.Component::class.java.packageName,
                dagger.Component::class.java.simpleName,
            )

        val annotationBuilder = AnnotationSpec.builder(componentClassName)

        if (component.abstractModules.isNotEmpty()) {
            annotationBuilder.addMember(
                "modules = [%L]",
                component.abstractModules.joinToString(", ") { "${it.name}::class" },
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

    private fun mapAbstractModules(modules: List<AbstractModule>): List<TypeSpec> {
        return modules.map { module ->
            val moduleBuilder =
                TypeSpec.classBuilder(module.name)
                    .addModifiers(KModifier.ABSTRACT)
                    .addAnnotation(
                        ClassName(
                            dagger.Module::class.java.packageName,
                            dagger.Module::class.java.simpleName,
                        ),
                    )
            module.binds.forEach { bind ->
                moduleBuilder.addFunction(createFunctionSpec(bind))
            }
            moduleBuilder.build()
        }
    }

    private fun createFunctionSpec(bind: Bind): FunSpec {
        val funSpec =
            FunSpec.builder("bind${bind.bindTypes.type.name}")
                .addModifiers(KModifier.ABSTRACT)
                .addParameter(
                    "impl",
                    ClassName(
                        bind.bindTypes.impl.packageName,
                        bind.bindTypes.impl.name,
                    ),
                )
                .returns(
                    ClassName(
                        bind.bindTypes.type.packageName,
                        bind.bindTypes.type.name,
                    ),
                ).addAnnotation(
                    ClassName(
                        dagger.Binds::class.java.packageName,
                        dagger.Binds::class.java.simpleName,
                    ),
                )

        if (bind.isSingleton) {
            funSpec.addAnnotation(
                ClassName(
                    Singleton::class.java.packageName,
                    Singleton::class.java.simpleName,
                ),
            )
        }

        return funSpec.build()
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
