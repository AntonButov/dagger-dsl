package transformers

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import models.Provides
import models.ProvidesModule

/**
 * Interface for mapping Module objects to KotlinPoet TypeSpec objects.
 */
interface ModuleToTypeSpecMapper {
    fun map(providesModules: List<ProvidesModule>): List<TypeSpec>
}

class ModuleToTypeSpecMapperImpl : ModuleToTypeSpecMapper {
    /**
     * Maps a list of Module objects to a list of TypeSpec objects.
     *
     * @param providesModules The list of Module objects to map
     * @return A list of TypeSpec objects representing Dagger modules
     */
    override fun map(providesModules: List<ProvidesModule>): List<TypeSpec> {
        if (providesModules.isEmpty()) {
            return emptyList()
        }

        return providesModules.map { module ->

            val moduleBuilder =
                TypeSpec.classBuilder(module.name)
                    .addAnnotation(
                        AnnotationSpec.builder(ClassName(dagger.Module::class.java.packageName, dagger.Module::class.java.simpleName))
                            .build(),
                    )

            module.provides.forEach { provides ->
                moduleBuilder.addFunction(createProvidesFunction(provides))
            }

            moduleBuilder.build()
        }
    }

    /**
     * Creates a provides function for a Dagger module.
     *
     * @param provides The Provides object containing the information
     * @return A FunSpec representing a Dagger @Provides method
     */
    private fun createProvidesFunction(provides: Provides): FunSpec {
        val funBuilder =
            FunSpec.builder("provides${provides.type.name}")
                .addAnnotation(ClassName(dagger.Provides::class.java.packageName, dagger.Provides::class.java.simpleName))
                .returns(
                    ClassName(
                        provides.type.packageName,
                        provides.type.name,
                    ),
                )

        if (provides.isSingleton) {
            funBuilder.addAnnotation(ClassName("javax.inject", "Singleton"))
        }

        provides.paramTypes.forEach { paramTypes ->
            val paramName = paramTypes.name.replaceFirstChar { it.lowercase() }
            funBuilder.addParameter(
                ParameterSpec.builder(
                    paramName,
                    ClassName(
                        paramTypes.packageName,
                        paramTypes.name,
                    ),
                ).build(),
            )
        }

        var bodyContent = provides.body

        provides.paramTypes.forEach { paramTypes ->
            val paramName = paramTypes.name.replaceFirstChar { it.lowercase() }
            bodyContent = bodyContent.replace("get<${paramTypes.name}>()", paramName)
        }

        funBuilder.addCode("val result = $bodyContent \n return result()\n")

        return funBuilder.build()
    }
}
