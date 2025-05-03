package processor.psiUtils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class PsiUtilsTest : BehaviorSpec({
    Given("String.findFunctions extension") {
        When("parsing dagger-dsl code") {
            val code =
                """
                @DaggerDsl
                fun anyNameFunction() {
                    component {
                        get<CoffeeLogger>()
                        inject<Fragment>()
                        bind<Interface, Impl>()
                        produce<Impl>() {
                            Impl(get<Type>(), get<Type>())
                        }
                    }
                }
                """.trimIndent()

            Then("should correctly parse the DSL structure") {
                val methods = code.findFunctions()
                methods shouldHaveSize 1
                val mainFunction = methods[0]
                mainFunction.name shouldBe "anyNameFunction"

                // Check component call
                mainFunction.lambdaMethods shouldHaveSize 1
                val componentMethod = mainFunction.lambdaMethods[0]
                componentMethod.name shouldBe "component"

                // Check methods inside component
                componentMethod.lambdaMethods shouldHaveSize 4

                // Check get method
                val getMethod = componentMethod.lambdaMethods[0]
                getMethod.name shouldBe "get"
                getMethod.genericTypes shouldContainExactly listOf("CoffeeLogger")

                // Check inject method
                val injectMethod = componentMethod.lambdaMethods[1]
                injectMethod.name shouldBe "inject"
                injectMethod.genericTypes shouldContainExactly listOf("Fragment")

                // Check bind method
                val bindMethod = componentMethod.lambdaMethods[2]
                bindMethod.name shouldBe "bind"
                bindMethod.genericTypes shouldContainExactly listOf("Interface", "Impl")

                // Check produce method
                val produceMethod = componentMethod.lambdaMethods[3]
                produceMethod.name shouldBe "produce"
                produceMethod.genericTypes shouldContainExactly listOf("Impl")

                // Check nested get calls inside produce
                produceMethod.lambdaMethods shouldHaveSize 1
                val implCall = produceMethod.lambdaMethods[0]
                implCall.name shouldBe "Impl"
            }
        }

        When("parsing dagger-dsl code val") {
            val code =
                """
                @DaggerDsl
                val component = component {
                            get<CoffeeLogger>()
                            inject<Fragment>()
                            bind<Interface, Impl>()
                            produce<Impl>() {
                                Impl(get<Type>(), get<Type>())
                            }
                }
                """.trimIndent()

            Then("should correctly parse the DSL structure") {
                val methods = code.findFunctions()
                methods shouldHaveSize 1
                val mainFunction = methods[0]
                mainFunction.name shouldBe "component"

                mainFunction.lambdaMethods shouldHaveSize 1
                val componentMethod = mainFunction.lambdaMethods[0]
                componentMethod.name shouldBe "component"

                componentMethod.lambdaMethods shouldHaveSize 4

                val getMethod = componentMethod.lambdaMethods[0]
                getMethod.name shouldBe "get"
                getMethod.genericTypes shouldContainExactly listOf("CoffeeLogger")

                val injectMethod = componentMethod.lambdaMethods[1]
                injectMethod.name shouldBe "inject"
                injectMethod.genericTypes shouldContainExactly listOf("Fragment")

                val bindMethod = componentMethod.lambdaMethods[2]
                bindMethod.name shouldBe "bind"
                bindMethod.genericTypes shouldContainExactly listOf("Interface", "Impl")

                val produceMethod = componentMethod.lambdaMethods[3]
                produceMethod.name shouldBe "produce"
                produceMethod.genericTypes shouldContainExactly listOf("Impl")

                produceMethod.lambdaMethods shouldHaveSize 1
                val implCall = produceMethod.lambdaMethods[0]
                implCall.name shouldBe "Impl"
            }
        }
/*
        When("parsing dagger-dsl fun without body") {
            val code =
                """
                @DaggerDsl
                fun anyNameFunction() =
                    component {
                        get<CoffeeLogger>()
                        inject<Fragment>()
                        bind<Interface, Impl>()
                        produce<Impl>() {
                            Impl(get<Type>(), get<Type>())
                        }
                    }
                """.trimIndent()
            Then("should correctly parse the DSL structure") {
                val methods = code.findFunctions()
                methods shouldHaveSize 1
                val mainFunction = methods[0]
                mainFunction.name shouldBe "anyNameFunction"

                mainFunction.lambdaMethods shouldHaveSize 1
                val componentMethod = mainFunction.lambdaMethods[0]
                componentMethod.name shouldBe "component"

                componentMethod.lambdaMethods shouldHaveSize 4

                val getMethod = componentMethod.lambdaMethods[0]
                getMethod.name shouldBe "get"
                getMethod.genericTypes shouldContainExactly listOf("CoffeeLogger")

                val injectMethod = componentMethod.lambdaMethods[1]
                injectMethod.name shouldBe "inject"
                injectMethod.genericTypes shouldContainExactly listOf("Fragment")

                val bindMethod = componentMethod.lambdaMethods[2]
                bindMethod.name shouldBe "bind"
                bindMethod.genericTypes shouldContainExactly listOf("Interface", "Impl")

                val produceMethod = componentMethod.lambdaMethods[3]
                produceMethod.name shouldBe "produce"
                produceMethod.genericTypes shouldContainExactly listOf("Impl")

                produceMethod.lambdaMethods shouldHaveSize 1
                val implCall = produceMethod.lambdaMethods[0]
                implCall.name shouldBe "Impl"
            }
        }


 */
    }
})
