package usescases

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

interface Writer {
    fun write(
        packageName: String,
        fileName: String,
        component: String,
    )
}

class WriterImpl(private val codeGenerator: CodeGenerator) : Writer {
    override fun write(
        packageName: String,
        fileName: String,
        component: String,
    ) {
        val file =
            codeGenerator.createNewFile(
                dependencies = Dependencies(true),
                packageName = packageName,
                fileName = fileName,
            )
        file.bufferedWriter().use { writer ->
            writer.write(
                """
                package $packageName
                
                $component
                """.trimIndent(),
            )
        }
    }
}
