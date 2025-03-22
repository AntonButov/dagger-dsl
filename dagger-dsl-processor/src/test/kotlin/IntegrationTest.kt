package usescases.component

import dagger.dsl.core.Component
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import usescases.ClassLoaderImpl
import usescases.CompilerImpl
import java.io.File

class IntegrationTest : StringSpec({

    "simple file dsl should to compile, then load and call method should return data dsl object" {
        val compiler = CompilerImpl()
        val classLoader = ClassLoaderImpl()

        val classFile = compiler.compile(File("src/test/kotlin/test/dsl/SimpleCase.kt"))
        val result = classLoader.runStaticMethod(classFile, "test.dsl.SimpleCaseKt", "dsl") as Component

        result.name shouldBe "test"
    }
})
