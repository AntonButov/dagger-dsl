package transformers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.every
import io.mockk.mockk
import models.Component
import models.ComponentType
import models.ComponentTypeMethod
import models.Type

class MappersTest : BehaviorSpec({

    Given("component") {

        val abstractModuleToTypeSpecMapper =
            mockk<AbstractModuleToTypeSpecMapper> {
                every { mapToTypeSpec(any()) } returns emptyList()
            }
        val moduleToTypeSpecMapper =
            mockk<ModuleToTypeSpecMapper> {
                every { map(any()) } returns emptyList()
            }
        val mapper: ComponentToFileSpecMapper = ComponentToFileSpecMapperImpl(abstractModuleToTypeSpecMapper, moduleToTypeSpecMapper)

        When("component is simple") {
            val componentType =
                ComponentType(
                    name = "SimpleComponent",
                    methods = emptyList(),
                )
            val component =
                Component(
                    isSingleton = false,
                    componentType = componentType,
                    abstractModules = emptyList(),
                    providesModules = emptyList(),
                )
            val specs = mapper.mapToSpecForWriter(component)
            Then("check component generation") {

                specs.componentSpec.toString() shouldBe
                    """
                |@dagger.Component
                |public interface SimpleComponentDsl
                |
                    """.trimMargin()
            }
        }

        When("component hase params") {
            val methodType =
                Type(
                    name = "SomeType",
                    packageName = "com.example",
                )
            val componentType =
                ComponentType(
                    name = "ComponentWithParams",
                    methods =
                        listOf(
                            ComponentTypeMethod(
                                name = "method",
                                params = emptyList(),
                                returnType = methodType,
                            ),
                        ),
                )
            val component =
                Component(
                    isSingleton = false,
                    componentType = componentType,
                    abstractModules = emptyList(),
                    providesModules = emptyList(),
                )
            val specs = mapper.mapToSpecForWriter(component)
            Then("check component generation") {
                val componentSpec = specs.componentSpec.toString()
                componentSpec shouldContain "public fun method(): com.example.SomeType"
            }
        }
    }
})
