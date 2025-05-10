package transformers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import dagger.Binds
import dagger.Module
import models.AbstractModule
import models.Bind
import javax.inject.Singleton

interface AbstractModuleToTypeSpecMapper {
    fun mapToTypeSpec(modules: List<AbstractModule>): List<TypeSpec>
}

class AbstractModuleToTypeSpecMapperImpl : AbstractModuleToTypeSpecMapper {
    override fun mapToTypeSpec(modules: List<AbstractModule>): List<TypeSpec> {
        return modules.map { module ->
            val moduleBuilder =
                TypeSpec.classBuilder(module.name)
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName(Module::class.java.packageName, Module::class.java.simpleName))
                            .build(),
                    )
                    .addModifiers(com.squareup.kotlinpoet.KModifier.ABSTRACT)

            module.binds.forEach { bind ->
                moduleBuilder.addFunction(createBindFunction(bind))
            }

            moduleBuilder.build()
        }
    }

    private fun createBindFunction(bind: Bind): FunSpec {
        val funBuilder =
            FunSpec.builder("bind${bind.bindTypes.type.name}")
                .addModifiers(com.squareup.kotlinpoet.KModifier.ABSTRACT)
                .addAnnotation(
                    ClassName(
                        Binds::class.java.packageName,
                        Binds::class.java.simpleName,
                    ),
                )
                .returns(
                    ClassName(
                        bind.bindTypes.type.packageName,
                        bind.bindTypes.type.name,
                    ),
                )
                .addParameter(
                    "impl",
                    ClassName(
                        bind.bindTypes.impl.packageName,
                        bind.bindTypes.impl.name,
                    ),
                )

        if (bind.isSingleton) {
            funBuilder.addAnnotation(
                ClassName(
                    Singleton::class.java.packageName,
                    Singleton::class.java.simpleName,
                ),
            )
        }

        return funBuilder.build()
    }
}
