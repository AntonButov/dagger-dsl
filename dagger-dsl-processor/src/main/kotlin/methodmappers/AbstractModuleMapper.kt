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
    fun mapAbstractModule(
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
