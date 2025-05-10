package methodmappers

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import models.ComponentType
import psiUtils.Method
import typeFinders.ComponentTypeFinder

class ComponentMapperTests : BehaviorSpec({
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

        val abstractModuleMapper: AbstractModuleMapper = mockk(relaxed = true)
        val moduleMapper: ModuleMapper = mockk(relaxed = true)

        val methodToComponentMapper: MethodToComponentMapper =
            MethodToComponentMapperImpl(
                componentTypeFinder = componentTypeFinder,
                abstractModuleMapper = abstractModuleMapper,
                moduleMapper = moduleMapper,
            )

        When("Map method component") {
            val componentMethod =
                Method(
                    name = "component",
                    genericTypes = listOf("SomeComponent"),
                )
            val component = methodToComponentMapper.mapToComponent(componentMethod, resolver)
            Then("Get component") {
                component.name shouldBe "Component"
                component.componentType.name shouldBe "SomeComponent"
                component.isSingleton shouldBe false
            }
        }

        When("Map method componentSingleton") {
            val componentMethod =
                Method(
                    name = "componentSingleton",
                    genericTypes = listOf("SomeComponent"),
                )
            val component =
                methodToComponentMapper.mapToComponent(componentMethod, resolver)

            Then("Have component singleton") {
                component.isSingleton shouldBe true
            }
        }
    }
})
