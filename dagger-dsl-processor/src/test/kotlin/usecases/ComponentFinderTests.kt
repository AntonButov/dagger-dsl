package usecases

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import models.Bind
import models.BindTypes
import models.ComponentType
import models.Type
import processor.testutils.Method
import usecases.bindfinders.BindImplFinder
import usecases.bindfinders.BindTypeFinder

class ComponentFinderTests : BehaviorSpec({
    Given("Some component method.") {
        val resolver = mockk<Resolver>()
        val componentTypeFinder =
            mockk<ComponentTypeFinder> {
                every { findByName(any(), any()) } returns
                    ComponentType(
                        name = "SomeComponent",
                        methods = emptyList(),
                    )
            }
        val typeType =
            Type(
                name = "SomeComponent",
                packageName = "typePackage",
            )
        val bindTypeFinder =
            mockk<BindTypeFinder> {
                every { findByName(any(), any()) } returns
                    typeType
            }
        val implType =
            Type(
                name = "SomeImpl",
                packageName = "implPackage",
            )
        val bindImplFinder =
            mockk<BindImplFinder> {
                every { findByName(any(), any()) } returns
                    implType
            }

        val methodToComponentMapper: MethodToComponentMapper =
            MethodToComponentMapperImpl(
                componentTypeFinder = componentTypeFinder,
                bindTypeFinder = bindTypeFinder,
                bindImplFinder = bindImplFinder,
            )

        When("Have method component") {
            val componentMethod =
                Method(
                    name = "component",
                    genericTypes = listOf("SomeComponent"),
                )

            Then("map component") {
                val component = methodToComponentMapper.mapComponent(listOf(componentMethod), resolver)
                component.name shouldBe "Component"
                component.componentType.name shouldBe "SomeComponent"
                component.isSingleton shouldBe false
            }
        }

        When("Have method componentSingleton") {
            val componentMethod =
                Method(
                    name = "componentSingleton",
                    genericTypes = listOf("SomeComponent"),
                )

            Then("map component") {
                val component =
                    methodToComponentMapper.mapComponent(listOf(componentMethod), resolver)
                component.isSingleton shouldBe true
            }
        }

        When("Some bind is not singleton") {
            val bind =
                Method(
                    name = "bind",
                    genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                )
            val bindType = Type("SomeBindClass", "packageName")
            every { bindTypeFinder.findByName(any(), any()) } returns bindType

            Then("Should be bind") {
                val module = methodToComponentMapper.mapAbstractModule(listOf(bind), resolver)
                module.name shouldBe "ModuleSomeBindClass"
                module.binds shouldBe
                    listOf(
                        Bind(
                            isSingleton = false,
                            bindTypes =
                                BindTypes(
                                    type = bindType,
                                    impl = implType,
                                ),
                        ),
                    )
            }
        }

        When("Singleton bind") {
            val bind =
                Method(
                    name = "bindSingleton",
                    genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                )
            val bindType = Type("SomeBindClass", "packageName")
            every { bindTypeFinder.findByName(any(), any()) } returns bindType

            Then("isSingleton should be true") {
                val module = methodToComponentMapper.mapAbstractModule(listOf(bind), resolver)
                module.name shouldBe "ModuleSomeBindClass"
                module.binds shouldBe
                    listOf(
                        Bind(
                            isSingleton = true,
                            bindTypes =
                                BindTypes(
                                    type = bindType,
                                    impl = implType,
                                ),
                        ),
                    )
            }
        }
    }
})
