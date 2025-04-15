package usecases

import com.google.devtools.ksp.processing.Resolver
import models.AbstractModule
import models.Bind
import models.BindTypes
import models.Component
import processor.testutils.Method
import usecases.bindfinders.BindImplFinder
import usecases.bindfinders.BindTypeFinder

interface MethodToComponentMapper {
    fun map(
        methods: List<Method>,
        resolver: Resolver,
    ): List<Any>

    fun mapComponent(
        methods: List<Method>,
        resolver: Resolver,
    ): Component

    fun mapAbstractModule(
        methods: List<Method>,
        resolver: Resolver,
    ): AbstractModule
}

@Suppress("UNCHECKED_CAST")
class MethodToComponentMapperImpl(
    private val componentTypeFinder: ComponentTypeFinder,
    private val bindTypeFinder: BindTypeFinder,
    private val bindImplFinder: BindImplFinder,
) : MethodToComponentMapper {
    override fun mapComponent(
        methods: List<Method>,
        resolver: Resolver,
    ): Component {
        if (methods.isEmpty()) {
            throw IllegalArgumentException("Methods should not be empty")
        }
        return (map(methods, resolver) as List<Component>).first()
    }

    override fun map(
        methods: List<Method>,
        resolver: Resolver,
    ): List<Any> {
        return methods.map { method ->
            when (method.name) {
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

                "moduleAbstract" -> {
                    mapAbstractModule(method.lambdaMethods, resolver)
                }

                else -> error("Unsupported method '${method.name}')")
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

        return Component(
            isSingleton = isSingleton,
            componentType = componentType,
            abstractModules = map(method.lambdaMethods, resolver) as List<AbstractModule>,
        )
    }

    override fun mapAbstractModule(
        methods: List<Method>,
        resolver: Resolver,
    ): AbstractModule {
        val binds = mutableListOf<Bind>()
        methods.forEach { method ->
            when (method.name) {
                "bind" -> {
                    processBind(
                        method = method,
                        resolver = resolver,
                        binds = binds,
                        isSingleton = false,
                    )
                }
                "bindSingleton" -> {
                    processBind(
                        method = method,
                        resolver = resolver,
                        binds = binds,
                        isSingleton = true,
                    )
                }
                else -> error("Method not supported.")
            }
        }
        return AbstractModule(
            binds = binds,
        )
    }

    private fun processBind(
        method: Method,
        resolver: Resolver,
        binds: MutableList<Bind>,
        isSingleton: Boolean,
    ) {
        val type = method.genericTypes.first()
        val impl = method.genericTypes.last()
        val typeType = bindTypeFinder.findByName(resolver, type)
        val implType = bindImplFinder.findByName(resolver, impl)
        binds.add(
            Bind(
                isSingleton = isSingleton,
                bindTypes = BindTypes(typeType, implType),
            ),
        )
    }
}
