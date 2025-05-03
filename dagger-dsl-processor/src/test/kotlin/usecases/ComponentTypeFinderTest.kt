package usecases

import compile
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import models.ComponentTypeMethod
import models.Type
import typeFinders.ComponentTypeFinderImpl

class ComponentTypeFinderTest : BehaviorSpec(
    {

        Given("component type name is SimpleComponent and code") {
            val componentTypeName = "CoffeeShop"
            val code =
                """
                class Code @Inject constructor()

                interface CoffeeShop {
                    fun maker(): String
                    fun logger(): String
                }

                @DaggerDsl
                fun anyNameFunction() {
                    component<CoffeeShop> {
                    }
                }
                """.trimIndent()
            When("findByName") {
                Then("should return the component type") {
                    code.compile { resolver ->
                        val componentTypeFinder = ComponentTypeFinderImpl()
                        val componentType = componentTypeFinder.findByName(resolver, componentTypeName)
                        componentType.name shouldBe "CoffeeShop"
                        componentType.methods shouldBe
                            listOf(
                                ComponentTypeMethod(
                                    name = "maker",
                                    params = emptyList(),
                                    returnType =
                                        Type(
                                            name = "String",
                                            "kotlin",
                                        ),
                                ),
                                ComponentTypeMethod(
                                    name = "logger",
                                    params = emptyList(),
                                    returnType =
                                        Type(
                                            name = "String",
                                            "kotlin",
                                        ),
                                ),
                            )
                    }
                }
            }
        }
    },
)
