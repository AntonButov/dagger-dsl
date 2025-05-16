package methodmappers

import com.google.devtools.ksp.processing.Resolver
import models.Component
import org.jetbrains.kotlin.javax.inject.Inject
import psiUtils.Method
import typeFinders.ComponentTypeFinder

interface MethodToComponentMapper {
    fun mapToComponent(
        method: Method,
        resolver: Resolver,
    ): Component
}

class MethodToComponentMapperImpl
    @Inject
    constructor(
        private val componentTypeFinder: ComponentTypeFinder,
        private val abstractModuleMapper: AbstractModuleMapper,
        private val moduleMapper: ModuleMapper,
        private val modulesMapper: ModulesMapper,
    ) : MethodToComponentMapper {
        override fun mapToComponent(
            method: Method,
            resolver: Resolver,
        ): Component {
            return when (method.name) {
                "component" -> {
                    mapToComponent(
                        isSingleton = false,
                        method = method,
                        resolver = resolver,
                    )
                }
                "componentSingleton" -> {
                    mapToComponent(
                        isSingleton = true,
                        method = method,
                        resolver = resolver,
                    )
                }
                else -> {
                    error("Method ${method.name} is not supported")
                }
            }
        }

        private fun mapToComponent(
            isSingleton: Boolean,
            method: Method,
            resolver: Resolver,
        ): Component {
            val componentTypeName =
                method.genericTypes.firstOrNull()
                    ?: error("Component method must have at least one generic type")
            val componentType = componentTypeFinder.findByName(resolver, componentTypeName)

            val modulesClassicWay = modulesMapper.mapToModules(method.lambdaMethods, resolver)

            val rootAbstractModules = abstractModuleMapper.mapToAbstractModule(method.lambdaMethods, resolver)
            val rootProvidesModules = moduleMapper.mapModule(method.lambdaMethods, resolver)
            val resultAbstractModules =
                (modulesClassicWay.abstractModules + rootAbstractModules)
                    .filter { it.binds.isNotEmpty() }
                    .distinct() // https://github.com/AntonButov/dagger-dsl/issues/40
            val resultProvidesModules =
                (modulesClassicWay.providesModules + rootProvidesModules)
                    .filter { it.provides.isNotEmpty() }
                    .distinct()
            return Component(
                isSingleton = isSingleton,
                componentType = componentType,
                abstractModules = resultAbstractModules,
                providesModules = resultProvidesModules,
            )
        }
    }
