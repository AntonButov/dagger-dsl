package usecases.component

import compile
import io.kotest.common.runBlocking
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.mockk.mockk
import jardownloader.JarDownloaderImpl
import jardownloader.SystemPropertiesProviderImpl
import usecases.CompilerImpl
import usecases.findDaggerDslMainFunction
import java.io.File

class CompilerTest : StringSpec({

    val systemPropertiesProvider = SystemPropertiesProviderImpl()
    val okHttpClient = HttpClient(CIO)
    val jarDownloader = JarDownloaderImpl(mockk(relaxed = true), systemPropertiesProvider, okHttpClient)
    val compiler = CompilerImpl(jarDownloader, mockk(relaxed = true))

    "compile should return a file with the generated code" {
        val sourceCode =
            """
            import dagger.dsl.core.DaggerDsl
            import dagger.dsl.core.component
                
            @DaggerDsl    
            fun anyNameFunction() =
               component {
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

            val generatedFile = runBlocking { compiler.compile(File(dslFunction!!.containingFile.filePath)) }
            generatedFile.path shouldContain "build/tmp"
        }
    }
})

@Throws(IllegalStateException::class)
private fun File.isValid() {
    exists() shouldBe true
    path shouldContain "build/tmp"
}
