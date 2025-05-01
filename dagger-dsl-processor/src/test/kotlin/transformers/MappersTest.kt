package transformers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import models.Component
import models.ComponentType

class MappersTest : BehaviorSpec({

    Given("component") {

        val mapper: ComponentToFileSpecMapper = ComponentToFileSpecMapperImpl()

        When("component") {
            val componentType =
                ComponentType(
                    name = "SimpleComponent",
                    methods = emptyList(),
                )
            val component =
                Component(isSingleton = false, componentType = componentType, abstractModules = emptyList())
            val specs = mapper.map(component)
            Then("check component generation") {

                specs.componentSpec.toString() shouldBe
                    """
                |@dagger.Component
                |public interface SimpleComponentDsl
                |
                    """.trimMargin()
            }

            Then("check module generation") {
            }
        }
    }
})
