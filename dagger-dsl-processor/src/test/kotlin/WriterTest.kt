package usecases.component

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.symbol.KSFile
import io.kotest.core.spec.style.StringSpec
import io.mockk.mockk
import io.mockk.verify
import transformers.SpecsForWriter
import usecases.Writer
import usecases.WriterImpl

class WriterTest : StringSpec({

    lateinit var codeGenerator: CodeGenerator
    lateinit var writer: Writer

    beforeEach {
        codeGenerator = mockk<CodeGenerator>(relaxed = true)
        writer = WriterImpl(codeGenerator)
    }

    "should create a new file with the correct content" {
        val file = mockk<KSFile>(relaxed = true)
        val component = mockk<SpecsForWriter>(relaxed = true)
        writer.write(file, component)

        verify {
            codeGenerator.createNewFile(
                dependencies = any(),
                packageName = any(),
                fileName = any(),
            )
        }
    }
})
