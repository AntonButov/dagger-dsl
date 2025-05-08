package methodmappers

import com.google.devtools.ksp.processing.Resolver
import models.AbstractModule
import models.Component
import models.ProvidesModule
import org.jetbrains.kotlin.javax.inject.Inject
import psiUtils.Method
import typeFinders.ComponentTypeFinder

interface MethodToComponentMapper {
    fun mapComponent(
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
    ) : MethodToComponentMapper {
        override fun mapComponent(
            method: Method,
            resolver: Resolver,
        ): Component {
            return when (method.name) {
                "component" -> {
                    mapComponent(
                        isSingleton = false,
                        method = method,
                        resolver = resolver,
                    )
                }
                "componentSingleton" -> {
                    mapComponent(
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

        private fun mapComponent(
            isSingleton: Boolean,
            method: Method,
            resolver: Resolver,
        ): Component {
            val componentTypeName = method.genericTypes.first()
            val componentType = componentTypeFinder.findByName(resolver, componentTypeName)
            val modules = mapModules(method.lambdaMethods, resolver)
            return Component(
                isSingleton = isSingleton,
                componentType = componentType,
                abstractModules = modules.abstractModules,
                providesModules = modules.providesModules,
            )
        }

        private fun mapModules(
            methods: List<Method>,
            resolver: Resolver,
        ): ModulesContainer {
            val abstractModules = mutableListOf<AbstractModule>()
            val providesModules = mutableListOf<ProvidesModule>()
            methods.forEach { method ->
                when (method.name) {
                    "moduleAbstract" -> {
                        abstractModules.add(abstractModuleMapper.mapAbstractModule(method.lambdaMethods, resolver))
                    }

                    "module" -> {
                        providesModules.add(moduleMapper.mapModule(method.lambdaMethods, resolver))
                    }

                    else -> error("Unsupported method '${method.name}')")
                }
            }
            return ModulesContainer(abstractModules, providesModules)
        }

        private class ModulesContainer(
            val abstractModules: List<AbstractModule>,
            val providesModules: List<ProvidesModule>,
        )
    }
