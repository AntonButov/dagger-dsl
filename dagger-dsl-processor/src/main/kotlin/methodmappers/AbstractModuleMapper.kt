package methodmappers

import com.google.devtools.ksp.processing.Resolver
import models.AbstractModule
import models.Bind
import models.BindTypes
import org.jetbrains.kotlin.javax.inject.Inject
import psiUtils.Method
import typeFinders.BindImplFinder
import typeFinders.BindTypeFinder

interface AbstractModuleMapper {
    fun mapToAbstractModule(
        methods: List<Method>,
        resolver: Resolver,
    ): AbstractModule
}

class AbstractModuleMapperImpl
    @Inject
    constructor(
        private val bindTypeFinder: BindTypeFinder,
        private val bindImplFinder: BindImplFinder,
    ) : AbstractModuleMapper {
        override fun mapToAbstractModule(
            methods: List<Method>,
            resolver: Resolver,
        ): AbstractModule {
            val binds = mutableListOf<Bind>()
            methods.forEach { method ->
                when (method.name) {
                    "bind" -> {
                        binds.add(
                            method.mapToBind(
                                resolver = resolver,
                                isSingleton = false,
                            ),
                        )
                    }
                    "bindSingleton" -> {
                        binds.add(
                            method.mapToBind(
                                resolver = resolver,
                                isSingleton = true,
                            ),
                        )
                    }
                }
            }
            return AbstractModule(
                binds = binds,
            )
        }

        private fun Method.mapToBind(
            resolver: Resolver,
            isSingleton: Boolean,
        ): Bind {
            val generics = this.genericTypes
            require(generics.size == 2) {
                "bind[Singleton] must specify exactly two generic types: <Interface, Implementation>"
            }
            val type = generics[0]
            val impl = generics[1]
            val typeType = bindTypeFinder.findByName(resolver, type)
            val implType = bindImplFinder.findByName(resolver, impl)
            return Bind(
                isSingleton = isSingleton,
                bindTypes = BindTypes(typeType, implType),
            )
        }
    }
