package usecases

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtProperty
import psiUtils.PsiFileCreatorImpl

/**
 * Utility function to parse Kotlin code and get declarations
 */
private fun declarations(code: String): List<KtDeclaration> {
    val psiFileCreator = PsiFileCreatorImpl()
    val psiFile = psiFileCreator.createKtFile(code)
    return psiFile.declarations
}

class DeclarationsTest : BehaviorSpec({
    Given("declarations function") {
        When("parsing code with a single function") {
            val code =
                """
                fun testFunction() {
                    println("Hello, world!")
                }
                """.trimIndent()

            val result = declarations(code)

            Then("should return a list with one function declaration") {
                result shouldHaveSize 1
                val function = result.first() as KtNamedFunction
                function.name shouldBe "testFunction"
            }
        }

        When("parsing code with multiple declarations") {
            val code =
                """
                class TestClass {
                    fun method() {}
                }
                
                val property = "test"
                
                fun testFunction() {
                    println("Hello, world!")
                }
                """.trimIndent()

            val result = declarations(code)

            Then("should return a list with all declarations in order") {
                result shouldHaveSize 3

                val klass = result[0] as KtClass
                klass.name shouldBe "TestClass"

                val property = result[1] as KtProperty
                property.name shouldBe "property"

                val function = result[2] as KtNamedFunction
                function.name shouldBe "testFunction"
            }
        }

        When("parsing code with imports and package declaration") {
            val code =
                """
                package test.package
                
                import kotlin.io.println
                
                fun testFunction() {
                    println("Hello, world!")
                }
                """.trimIndent()

            val result = declarations(code)

            Then("should return only the actual declarations, not imports or package") {
                result shouldHaveSize 1
                val function = result.first() as KtNamedFunction
                function.name shouldBe "testFunction"
            }
        }

        When("parsing empty code") {
            val code = ""

            val result = declarations(code)

            Then("should return an empty list") {
                result shouldHaveSize 0
            }
        }

        When("parsing code with comments only") {
            val code =
                """
                // This is a comment
                /* This is a block comment */
                """.trimIndent()

            val result = declarations(code)

            Then("should return an empty list") {
                result shouldHaveSize 0
            }
        }
    }
})
