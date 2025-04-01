package usescases

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies

interface Writer {
    /**
     * Writes a Kotlin source file with a package declaration and the provided component content.
     *
     * This method creates a new file in the specified package using a code generator (with dependencies enabled),
     * then writes a formatted Kotlin source including the package declaration followed by the component.
     *
     * @param packageName the package in which the file will be created.
     * @param fileName the name of the file to generate.
     * @param component the Kotlin code content to be written into the file.
     */
    fun write(
        packageName: String,
        fileName: String,
        component: String,
    )
}

class WriterImpl(private val codeGenerator: CodeGenerator) : Writer {
    /**
     * Writes a Kotlin file with the specified package declaration and component content.
     *
     * This implementation creates a new file with dependencies enabled using the provided code generator, then writes a file starting with the package declaration followed by the specified component code.
     *
     * @param packageName the package name to be declared at the top of the file.
     * @param fileName the name of the file to be created.
     * @param component the component code to be written after the package declaration.
     */
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
