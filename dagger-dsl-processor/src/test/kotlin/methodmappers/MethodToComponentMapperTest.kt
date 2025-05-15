package methodmappers

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import models.AbstractModule
import models.ComponentType
import models.ProvidesModule
import psiUtils.Method
import typeFinders.ComponentTypeFinder

class MethodToComponentMapperTest : BehaviorSpec({

    given("MethodToComponentMapper with mocked dependencies") {
        val resolver = mockk<Resolver>()
        val componentTypeFinder =
            mockk<ComponentTypeFinder> {
                every { findByName(resolver, any()) } returns ComponentType("ComponentType", listOf())
            }
        val abstractModuleMapper =
            mockk<AbstractModuleMapper> {
                every { mapToAbstractModule(any(), resolver) } returns
                    AbstractModule(
                        binds = listOf(mockk()),
                    )
            }
        val moduleMapper =
            mockk<ModuleMapper> {
                every {
                    mapModule(any(), resolver)
                } returns
                    ProvidesModule(
                        provides = listOf(mockk()),
                    )
            }

        val modulesMapper =
            mockk<ModulesMapper> {
                every { mapToModules(any(), resolver) } returns mockk(relaxed = true)
            }

        val mapper =
            MethodToComponentMapperImpl(
                componentTypeFinder = componentTypeFinder,
                abstractModuleMapper = abstractModuleMapper,
                moduleMapper = moduleMapper,
                modulesMapper = modulesMapper,
            )

        `when`("processing different module methods") {
            val moduleAbstractMethod =
                Method(
                    name = "moduleAbstract",
                    lambdaMethods = listOf(),
                    genericTypes = listOf(),
                )

            val moduleMethod =
                Method(
                    name = "module",
                    lambdaMethods = listOf(),
                    genericTypes = listOf(),
                )

            val methodComponent =
                Method(
                    name = "component",
                    lambdaMethods = listOf(moduleMethod, moduleAbstractMethod),
                    genericTypes = listOf("SomeType"),
                )

            val result = mapper.mapToComponent(methodComponent, resolver)

            then("should correctly process moduleAbstract method") {
                verify(exactly = 1) {
                    abstractModuleMapper.mapToAbstractModule(any(), resolver)
                }
                result.abstractModules shouldHaveSize 1
            }

            then("should correctly process module method") {
                verify(exactly = 1) {
                    moduleMapper.mapModule(any(), resolver)
                }

                result.providesModules shouldHaveSize 1
            }

            then("modules mapToModules should be called") {
                verify(exactly = 1) {
                    modulesMapper.mapToModules(any(), resolver)
                }
            }

            then("component should be not singleton") {
                result.isSingleton shouldBe false
            }
        }
    }
})
