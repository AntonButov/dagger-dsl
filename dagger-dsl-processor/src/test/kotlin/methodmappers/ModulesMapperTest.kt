package methodmappers

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import models.AbstractModule
import models.ProvidesModule
import psiUtils.Method

class ModulesMapperTest : BehaviorSpec({

    lateinit var resolver: Resolver
    lateinit var abstractModuleMapper: AbstractModuleMapper
    lateinit var moduleMapper: ModuleMapper
    lateinit var mapper: ModulesMapperImpl
    lateinit var abstractModuleMock: AbstractModule
    lateinit var providesModuleMock: ProvidesModule

    given("ModulesMapper with mocked dependencies") {

        beforeContainer {
            resolver = mockk()
            abstractModuleMapper = mockk()
            moduleMapper = mockk()

            mapper =
                ModulesMapperImpl(
                    abstractModuleMapper = abstractModuleMapper,
                    moduleMapper = moduleMapper,
                )

            abstractModuleMock = mockk()
            providesModuleMock = mockk()

            every {
                abstractModuleMapper.mapToAbstractModule(any(), any())
            } returns abstractModuleMock

            every {
                moduleMapper.mapModule(any(), any())
            } returns providesModuleMock
        }

        `when`("mapping to modules") {
            val result = mapper.mapToModules(emptyList(), resolver)

            then("should return empty modules container") {
                result.abstractModules.shouldBeEmpty()
                result.providesModules.shouldBeEmpty()
            }
        }

        `when`("mapping to modules") {
            val moduleAbstractMethod =
                Method(
                    name = "moduleAbstract",
                    lambdaMethods = listOf(),
                    genericTypes = listOf(),
                )

            val result = mapper.mapToModules(listOf(moduleAbstractMethod), resolver)

            then("should process moduleAbstract correctly") {
                verify(exactly = 1) {
                    abstractModuleMapper.mapToAbstractModule(moduleAbstractMethod.lambdaMethods, resolver)
                }
                result.abstractModules shouldHaveSize 1
                result.abstractModules[0] shouldBe abstractModuleMock
                result.providesModules.shouldBeEmpty()
            }
        }

        `when`("mapping to modules") {
            val moduleMethod =
                Method(
                    name = "module",
                    lambdaMethods = listOf(),
                    genericTypes = listOf(),
                )

            val result = mapper.mapToModules(listOf(moduleMethod), resolver)

            then("should process module correctly") {
                verify(exactly = 1) {
                    moduleMapper.mapModule(moduleMethod.lambdaMethods, resolver)
                }
                result.providesModules shouldHaveSize 1
                result.providesModules[0] shouldBe providesModuleMock
                result.abstractModules.shouldBeEmpty()
            }
        }

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

        `when`("mapping to modules") {
            val methods = listOf(moduleAbstractMethod, moduleMethod)
            val result = mapper.mapToModules(methods, resolver)

            then("should process all known method types correctly") {
                verify(exactly = 1) {
                    abstractModuleMapper.mapToAbstractModule(moduleAbstractMethod.lambdaMethods, resolver)
                }

                verify(exactly = 1) {
                    moduleMapper.mapModule(moduleMethod.lambdaMethods, resolver)
                }

                result.abstractModules shouldHaveSize 1
                result.providesModules shouldHaveSize 1
            }
        }

        `when`("mapping to modules") {
            val nestedMethods =
                listOf(
                    Method(name = "nested1", lambdaMethods = listOf(), genericTypes = listOf()),
                    Method(name = "nested2", lambdaMethods = listOf(), genericTypes = listOf()),
                )

            val moduleAbstractMethod =
                Method(
                    name = "moduleAbstract",
                    lambdaMethods = nestedMethods,
                    genericTypes = listOf(),
                )

            val moduleMethod =
                Method(
                    name = "module",
                    lambdaMethods = nestedMethods,
                    genericTypes = listOf(),
                )

            val methods = listOf(moduleAbstractMethod, moduleMethod)
            mapper.mapToModules(methods, resolver)

            then("should pass nested methods to specific mappers") {
                verify(exactly = 1) {
                    abstractModuleMapper.mapToAbstractModule(nestedMethods, resolver)
                }

                verify(exactly = 1) {
                    moduleMapper.mapModule(nestedMethods, resolver)
                }
            }
        }
    }
})
