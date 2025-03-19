package usescases.component

import compile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import usescases.CompilerImpl
import usescases.findDaggerDslMainFunction
import java.io.File

class CompilerTest : StringSpec({

    val compiler = CompilerImpl()

    "compile should return a file with the generated code" {
        val sourceCode =
            """
            import dagger.dsl.core.DaggerDsl
            import dagger.dsl.core.component
                
            @DaggerDsl    
            fun anyNameFunction() {
               component {
               }
            }
            """.trimIndent()

        val generatedFile = compiler.compile(sourceCode)

        generatedFile.exists() shouldBe true
        generatedFile.path shouldContain "build/tmp"
    }

    "file test" {
        val sourceFile = File("src/test/kotlin/test/dsl/SimpleCase.kt")

        val generatedFile = compiler.compile(sourceFile)

        generatedFile.isValid()
    }

    "file test with resolver" {
        val sourceFile = File("src/test/kotlin/test/dsl/SimpleCase.kt")

        sourceFile compile { resolver ->
            val dslFunction = resolver.findDaggerDslMainFunction()

            val generatedFile = compiler.compile(File(dslFunction.containingFile!!.filePath))
            generatedFile.path shouldContain "build/tmp"
        }
    }
})

@Throws(IllegalStateException::class)
private fun File.isValid() {
    exists() shouldBe true
    path shouldContain "build/tmp"
}
