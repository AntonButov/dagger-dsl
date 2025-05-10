package psiUtils

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtCallExpression

class MethodFromKtCallExpressionTest : BehaviorSpec({

    Given("Method.fromKtCallExpression") {
        val psiFileCreator = PsiFileCreatorImpl()

        When("Converting a simple call expression without arguments") {
            val code =
                """
                fun testFunction() {
                    println()
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with correct name and empty generic types") {
                method.name shouldBe "println"
                method.genericTypes.shouldBeEmpty()
                method.lambdaMethods.shouldBeEmpty()
                method.lambdaBody shouldBe ""
            }
        }

        When("Converting a call expression with generic type arguments") {
            val code =
                """
                fun testFunction() {
                    listOf<String>()
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with correct generic types") {
                method.name shouldBe "listOf"
                method.genericTypes shouldBe listOf("String")
                method.lambdaMethods.shouldBeEmpty()
            }
        }

        When("Converting a call expression with multiple generic type arguments") {
            val code =
                """
                fun testFunction() {
                    mapOf<String, Int>()
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with multiple generic types") {
                method.name shouldBe "mapOf"
                method.genericTypes shouldBe listOf("String", "Int")
                method.lambdaMethods.shouldBeEmpty()
            }
        }

        When("Converting a call expression with a lambda argument") {
            val code =
                """
                fun testFunction() {
                    runBlocking {
                        println("Hello")
                    }
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with lambda body and nested methods") {
                method.name shouldBe "runBlocking"
                method.lambdaMethods shouldHaveSize 1
                method.lambdaMethods[0].name shouldBe "println"
                method.lambdaBody.contains("println") shouldBe true
            }
        }

        When("Converting a call expression with nested lambda calls") {
            val code =
                """
                fun testFunction() {
                    component<TestComponent> {
                        bind<Interface1>()
                        bind<Interface2>()
                    }
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with nested structure") {
                method.name shouldBe "component"
                method.genericTypes shouldBe listOf("TestComponent")
                method.lambdaMethods shouldHaveSize 2

                method.lambdaMethods[0].name shouldBe "bind"
                method.lambdaMethods[0].genericTypes shouldBe listOf("Interface1")

                method.lambdaMethods[1].name shouldBe "bind"
                method.lambdaMethods[1].genericTypes shouldBe listOf("Interface2")
            }
        }

        When("Converting a call expression with deeply nested lambda calls") {
            val code =
                """
                fun testFunction() {
                    component<TestComponent> {
                        bind<Interface1, Implementation1>()
                    }
                }
                """.trimIndent()

            val function = code.toDeclarations().first()
            val bodyExpression = (function as org.jetbrains.kotlin.psi.KtNamedFunction).bodyExpression as org.jetbrains.kotlin.psi.KtBlockExpression
            val callExpression = bodyExpression.statements.first() as KtCallExpression

            val method = Method.fromKtCallExpression(callExpression)

            Then("Should create a Method with deeply nested structure") {
                method.name shouldBe "component"
                method.genericTypes shouldBe listOf("TestComponent")
                method.lambdaMethods shouldHaveSize 1

                val bindMethod = method.lambdaMethods[0]
                bindMethod.name shouldBe "bind"
                bindMethod.genericTypes shouldBe listOf("Interface1", "Implementation1")
            }
        }
    }
})
