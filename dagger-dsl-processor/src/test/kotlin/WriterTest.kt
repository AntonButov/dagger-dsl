package usescases.component

import com.google.devtools.ksp.processing.CodeGenerator
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import usescases.Writer
import usescases.WriterImpl

class WriterTest : StringSpec({

    lateinit var codeGenerator: CodeGenerator
    lateinit var writer: Writer

    beforeEach {
        codeGenerator = mockk<CodeGenerator>(relaxed = true)
        writer = WriterImpl(codeGenerator)
    }

    "should create a new file with the correct content" {
        val packageName = "com.example"
        val fileName = "Di.kt"
        val component = "MyComponent"
        writer.write(packageName, fileName, component)

        verify {
            codeGenerator.createNewFile(
                dependencies = any(),
                packageName = packageName,
                fileName = "Di.kt",
            )
        }
    }
})
