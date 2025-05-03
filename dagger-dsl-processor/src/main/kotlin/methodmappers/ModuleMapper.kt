package methodmappers

import com.google.devtools.ksp.processing.Resolver
import models.Provides
import models.ProvidesModule
import processor.psiUtils.Method
import typeFinders.ClassAndInterfaceTypeFinder
import java.util.regex.Pattern

interface ModuleMapper {
    fun mapModule(
        methods: List<Method>,
        resolver: Resolver,
    ): ProvidesModule
}

class ModuleMapperImpl(
    private val classAndInterfaceTypeFinder: ClassAndInterfaceTypeFinder,
) : ModuleMapper {
    override fun mapModule(
        methods: List<Method>,
        resolver: Resolver,
    ): ProvidesModule {
        val provides = mutableListOf<Provides>()

        methods.forEach { method ->
            when (method.name) {
                "provides" -> {
                    val parameters = extractParameters(method.lambdaBody)
                    val parameterTypes =
                        parameters.map { parameter ->
                            classAndInterfaceTypeFinder.findByName(resolver, parameter)
                        }
                    val typeName =
                        method.genericTypes.firstOrNull()
                            ?: throw IllegalArgumentException("Provides method must have a type parameter")
                    val type = classAndInterfaceTypeFinder.findByName(resolver, typeName)
                    provides.add(
                        Provides(
                            isSingleton = false,
                            paramTypes = parameterTypes,
                            type = type,
                            body = method.lambdaBody,
                        ),
                    )
                }
                else -> throw IllegalArgumentException("Unsupported method: ${method.name}")
            }
        }

        return ProvidesModule(
            provides = provides,
        )
    }

    private fun extractParameters(body: String): List<String> {
        val parameters = mutableListOf<String>()

        // Pattern to match get<Type>() expressions
        val pattern = Pattern.compile("get<([A-Za-z0-9_]+)>\\(\\)")
        val matcher = pattern.matcher(body)

        while (matcher.find()) {
            val typeName = matcher.group(1)
            parameters.add(typeName)
        }

        return parameters
    }
}
