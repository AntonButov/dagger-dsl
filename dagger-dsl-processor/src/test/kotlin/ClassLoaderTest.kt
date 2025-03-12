package usescases.component

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import usescases.ClassLoaderImpl
import usescases.CompilerImpl

class ClassLoaderTest : StringSpec({
    val classLoader = ClassLoaderImpl()
    val compiler = CompilerImpl()
    val code =
        """
        class SomeClass {
            fun someMethod(): String {
                return "some string"
            }
        }
        """.trimIndent()

    "base test" {
        val classFile = compiler.compile(code, "build/tmp")
        val result = classLoader.runMethod(classFile, "SomeClass", "someMethod") as String
        result shouldBeEqual "some string"
    }
})
