package psiUtils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodFromKtNamedFunctionTest : BehaviorSpec({

    Given("Method companion object") {

        When("Converting a KtNamedFunction with empty body") {
            val code =
                """
                fun emptyFunction() {
                }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with correct name and empty lambda methods") {
                method.name shouldBe "emptyFunction"
                method.lambdaMethods.shouldBeEmpty()
                method.lambdaBody shouldBe "{\n}"
            }
        }

        When("Converting a KtNamedFunction with a single method call") {
            val code =
                """
                fun singleCallFunction() {
                    println("Hello")
                }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with one lambda method") {
                method.name shouldBe "singleCallFunction"
                method.lambdaMethods shouldHaveSize 1
                method.lambdaMethods[0].name shouldBe "println"
                method.lambdaBody.contains("println") shouldBe true
            }
        }

        When("Converting a KtNamedFunction with multiple method calls") {
            val code =
                """
                fun multipleCallsFunction() {
                    println("First")
                    println("Second")
                    println("Third")
                }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with multiple lambda methods") {
                method.name shouldBe "multipleCallsFunction"
                method.lambdaMethods shouldHaveSize 3
                method.lambdaMethods.all { it.name == "println" } shouldBe true
            }
        }

        When("Converting a KtNamedFunction with nested blocks") {
            val code =
                """
                fun nestedBlocksFunction() {
                    println("Outer")
                    if (true) {
                        println("Inner")
                    }
                }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with all method calls") {
                method.name shouldBe "nestedBlocksFunction"
                method.lambdaMethods shouldHaveSize 1 // Only direct calls in the function body
                method.lambdaMethods[0].name shouldBe "println"
            }
        }

        When("Converting a KtNamedFunction with no body") {
            val code =
                """
                fun noBodyFunction()
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with just the name") {
                method.name shouldBe "noBodyFunction"
                method.lambdaMethods.shouldBeEmpty()
                method.lambdaBody shouldBe ""
            }
        }

        When("Converting a KtNamedFunction with a DSL-style lambda") {
            val code =
                """
                fun dslFunction() {
                    component<TestComponent> {
                        bind<Interface1,Implementation1>() 
                        bind<Interface2,Implemenatation2>() 
                    }
                }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with nested structure") {
                method.name shouldBe "dslFunction"
                method.lambdaMethods shouldHaveSize 1

                val componentMethod = method.lambdaMethods[0]
                componentMethod.name shouldBe "component"
                componentMethod.genericTypes shouldBe listOf("TestComponent")

                // The component method should have bind methods inside
                componentMethod.lambdaMethods shouldHaveSize 2
                componentMethod.lambdaMethods[0].name shouldBe "bind"
                componentMethod.lambdaMethods[1].name shouldBe "bind"
            }
        }

        When("Converting a KtNamedFunction without body lambda") {
            val code =
                """
                fun dslFunction() =
                    component<TestComponent> {
                        bind<Interface1,Implementation1>() 
                        bind<Interface2,Implemenatation2>() 
                    }
                """.trimIndent()

            val function = code.toDeclarations().first() as KtNamedFunction
            val method = Method.fromKtNamedFunction(function)

            Then("Should create a Method with nested structure") {
                method.name shouldBe "dslFunction"
                method.lambdaMethods shouldHaveSize 1

                val componentMethod = method.lambdaMethods[0]
                componentMethod.name shouldBe "component"
                componentMethod.genericTypes shouldBe listOf("TestComponent")

                // The component method should have bind methods inside
                componentMethod.lambdaMethods shouldHaveSize 2
                componentMethod.lambdaMethods[0].name shouldBe "bind"
                componentMethod.lambdaMethods[1].name shouldBe "bind"
            }
        }
    }
})
