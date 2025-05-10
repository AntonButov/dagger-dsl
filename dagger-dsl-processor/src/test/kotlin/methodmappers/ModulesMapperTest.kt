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
import models.Bind
import models.BindTypes
import models.Provides
import models.ProvidesModule
import models.Type
import psiUtils.Method

class ModulesMapperTest : BehaviorSpec({

    Given("A ModulesMapper") {

        lateinit var resolver: Resolver
        lateinit var abstractModuleMapper: AbstractModuleMapper
        lateinit var moduleMapper: ModuleMapper
        lateinit var modulesMapper: ModulesMapperImpl

        beforeTest {
            resolver = mockk<Resolver>()

            abstractModuleMapper =
                mockk<AbstractModuleMapper> {
                    every { mapToAbstractModule(any(), any()) } returns
                        AbstractModule(
                            binds =
                                listOf(
                                    Bind(
                                        isSingleton = false,
                                        bindTypes =
                                            BindTypes(
                                                type = Type("Interface", "com.example"),
                                                impl = Type("Implementation", "com.example"),
                                            ),
                                    ),
                                ),
                        )
                }

            moduleMapper =
                mockk<ModuleMapper> {
                    every { mapModule(any(), any()) } returns
                        ProvidesModule(
                            provides =
                                listOf(
                                    Provides(
                                        isSingleton = false,
                                        paramTypes = emptyList(),
                                        type = Type("String", "kotlin"),
                                        body = "",
                                    ),
                                ),
                        )
                }

            modulesMapper =
                ModulesMapperImpl(
                    abstractModuleMapper = abstractModuleMapper,
                    moduleMapper = moduleMapper,
                )
        }

        When("Mapping an empty list of methods") {
            val result = modulesMapper.mapToModules(emptyList(), resolver)

            Then("Should return empty module containers") {
                result.abstractModules.shouldBeEmpty()
                result.providesModules.shouldBeEmpty()
            }
        }

        When("Mapping a list with an abstract module method") {
            val methods =
                listOf(
                    Method(
                        name = "moduleAbstract",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "bind",
                                    genericTypes = listOf("Interface", "Implementation"),
                                ),
                            ),
                    ),
                )

            Then("Should return a container with an abstract module") {
                val result = modulesMapper.mapToModules(methods, resolver)
                result.abstractModules shouldHaveSize 1
                result.providesModules.shouldBeEmpty()

                val abstractModule = result.abstractModules[0]
                abstractModule.binds shouldHaveSize 1
                abstractModule.binds[0].bindTypes.type.name shouldBe "Interface"

                verify {
                    abstractModuleMapper.mapToAbstractModule(any(), resolver)
                }
            }
        }

        When("Mapping a list with a provides module method") {
            val methods =
                listOf(
                    Method(
                        name = "module",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "provides",
                                    genericTypes = listOf("String"),
                                ),
                            ),
                    ),
                )

            Then("Should return a container with a provides module") {
                val result = modulesMapper.mapToModules(methods, resolver)
                result.abstractModules.shouldBeEmpty()
                result.providesModules shouldHaveSize 1

                val providesModule = result.providesModules[0]
                providesModule.provides shouldHaveSize 1
                providesModule.provides[0].type.name shouldBe "String"

                verify {
                    moduleMapper.mapModule(any(), resolver)
                }
            }
        }

        When("Mapping a list with both module types") {
            val methods =
                listOf(
                    Method(
                        name = "moduleAbstract",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "bind",
                                    genericTypes = listOf("Interface", "Implementation"),
                                ),
                            ),
                    ),
                    Method(
                        name = "module",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "provides",
                                    genericTypes = listOf("String"),
                                ),
                            ),
                    ),
                )

            Then("Should return a container with both module types") {
                val result = modulesMapper.mapToModules(methods, resolver)
                result.abstractModules shouldHaveSize 1
                result.providesModules shouldHaveSize 1

                verify {
                    abstractModuleMapper.mapToAbstractModule(any(), resolver)
                    moduleMapper.mapModule(any(), resolver)
                }
            }
        }

        When("Mapping a list with multiple modules of the same type") {
            val methods =
                listOf(
                    Method(
                        name = "moduleAbstract",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "bind",
                                    genericTypes = listOf("Interface1", "Implementation1"),
                                ),
                            ),
                    ),
                    Method(
                        name = "moduleAbstract",
                        lambdaMethods =
                            listOf(
                                Method(
                                    name = "bind",
                                    genericTypes = listOf("Interface2", "Implementation2"),
                                ),
                            ),
                    ),
                )

            Then("Should return a container with multiple modules") {
                val result = modulesMapper.mapToModules(methods, resolver)
                result.abstractModules shouldHaveSize 2
                result.providesModules.shouldBeEmpty()

                verify(exactly = 2) {
                    abstractModuleMapper.mapToAbstractModule(any(), resolver)
                }
            }
        }

        When("Mapping a list with methods that are not modules") {
            val methods =
                listOf(
                    Method(
                        name = "notAModule",
                        lambdaMethods = listOf(),
                    ),
                )

            Then("Should ignore non-module methods") {
                val result = modulesMapper.mapToModules(methods, resolver)
                result.abstractModules.shouldBeEmpty()
                result.providesModules.shouldBeEmpty()

                verify(exactly = 0) {
                    abstractModuleMapper.mapToAbstractModule(any(), any())
                    moduleMapper.mapModule(any(), any())
                }
            }
        }
    }
})
