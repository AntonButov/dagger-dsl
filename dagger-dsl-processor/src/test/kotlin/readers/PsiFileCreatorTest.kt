package readers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtFunction
import psiUtils.PsiFileCreatorImpl
import testutils.TestFileCreator

class PsiFileCreatorTest : BehaviorSpec({
    Given("PsiFileCreator") {
        val psiFileCreator = PsiFileCreatorImpl()

        When("createPsiFile") {
            val code =
                """
                fun main() {
                    println("Hello, world!")
                }
                """.trimIndent()
            val psiFile = psiFileCreator.createKtFile(code)
            Then("should return a PsiFile") {
                psiFile.text shouldBe code
                psiFile.declarations.first().node.psi as KtFunction
            }
        }

        When("createKtFileFromRealFile") {
            val code =
                """
                fun testFunction() {
                    println("Hello from real file!")
                }
                """.trimIndent()
            val tempFile = TestFileCreator.createTestFile(code)

            val psiFile = psiFileCreator.createKtFileFromRealFile(tempFile.absolutePath)

            Then("should return a PsiFile with content from the real file") {
                psiFile.text shouldBe code
                val function = psiFile.declarations.first().node.psi as KtFunction
                function.name shouldBe "testFunction"
            }
        }
    }
})
