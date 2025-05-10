package usecases

import compile
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import psiUtils.toMethods

class ComponentMethodFinderTest : BehaviorSpec({

    Given("CodeDslSplitterImpl") {
        val componentMethodFinder = ComponentMethodFinderImpl()

        When("splitDsl") {
            val code =
                """
                @DaggerDsl
                fun anyNameFunction() {
                    component {
                    }
                }

                fun otherFun() {

                }
                """.trimIndent()

            Then("should return a list of strings") {
                code compile { resolver ->
                    val dslFun = resolver.findDsl()!!
                    val component = componentMethodFinder.mapComponent(dslFun)
                    component.name shouldBe "component"
                    component.lambdaMethods shouldBe emptyList()
                }
            }
        }

        When("component hase generic") {
            val code =
                """
                @DaggerDsl
                fun anyNameFunction() {
                    
                        component<SomeComponent> {
                        }
                    }
                """.trimIndent()
            val component = code.toMethods().first().lambdaMethods.first()
            Then("should return the generic") {
                component.name shouldBe "component"
                component.genericTypes shouldBe listOf("SomeComponent")
                component.lambdaMethods shouldBe emptyList()
            }
        }
    }
})
