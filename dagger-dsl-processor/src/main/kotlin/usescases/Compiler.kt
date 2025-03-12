package usescases

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import java.io.File

interface Compiler {
    fun compile(
        sourceCode: String,
        outputDir: String,
    ): File
}

class CompilerImpl : Compiler {
    override fun compile(
        sourceCode: String,
        outPutPath: String,
    ): File {
        val outPutFile = File(outPutPath).apply { mkdir() }
        val sourceFile = File(outPutFile, "Generated.kt").apply { writeText(sourceCode) }
        val compiler = K2JVMCompiler()

        val args =
            K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = outPutFile.absolutePath
                classpathAsList = listOf(File("dagger-dsl-core.jar"), File("kotlin-stdlib-1.9.24.jar"))
            }

        val compilerMessageCollector =
            PrintingMessageCollector(
                System.out,
                MessageRenderer.GRADLE_STYLE,
                true,
            )

        val exitCode = compiler.exec(compilerMessageCollector, Services.EMPTY, args)
        if (exitCode != org.jetbrains.kotlin.cli.common.ExitCode.OK) {
            throw IllegalStateException("Compilation failed!")
        }

        //  sourceFile.delete()

        return outPutFile
    }
}
