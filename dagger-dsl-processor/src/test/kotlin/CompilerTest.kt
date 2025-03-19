package usescases.component

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import usescases.CompilerImpl

class CompilerTest : StringSpec({

    "compile should return a file with the generated code" {
        val compiler = CompilerImpl()
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

        val generatedFile = compiler.compile(sourceCode, "build/tmp")

        generatedFile.exists() shouldBe true
        generatedFile.path shouldContain "build/tmp"
    }
})
