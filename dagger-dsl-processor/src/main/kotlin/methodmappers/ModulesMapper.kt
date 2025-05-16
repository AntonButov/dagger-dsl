package methodmappers

import com.google.devtools.ksp.processing.Resolver
import models.AbstractModule
import models.ProvidesModule
import psiUtils.Method

interface ModulesMapper {
    fun mapToModules(
        methods: List<Method>,
        resolver: Resolver,
    ): ModulesContainer
}

class ModulesContainer(
    val abstractModules: List<AbstractModule>,
    val providesModules: List<ProvidesModule>,
)

class ModulesMapperImpl(
    private val abstractModuleMapper: AbstractModuleMapper,
    private val moduleMapper: ModuleMapper,
) : ModulesMapper {
    override fun mapToModules(
        methods: List<Method>,
        resolver: Resolver,
    ): ModulesContainer {
        val abstractModules = mutableListOf<AbstractModule>()
        val providesModules = mutableListOf<ProvidesModule>()

        methods.forEach { method ->
            when (method.name) {
                "moduleAbstract" -> {
                    abstractModules.add(abstractModuleMapper.mapToAbstractModule(method.lambdaMethods, resolver))
                }

                "module" -> {
                    providesModules.add(moduleMapper.mapModule(method.lambdaMethods, resolver))
                }
            }
        }

        return ModulesContainer(abstractModules, providesModules)
    }
}
