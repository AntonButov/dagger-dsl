package usescases

import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import java.io.File

interface Compiler {
    fun compile(sourceCode: String): File

    fun compile(sourceFile: File): File
}

private const val OUTPUT_DIR_NAME = "build/tmp"

class CompilerImpl : Compiler {
    private val outPutFile by lazy { File(OUTPUT_DIR_NAME).apply { mkdir() } }

    override fun compile(sourceCode: String): File {
        val sourceFile = File(outPutFile, "Generated.kt").apply { writeText(sourceCode) }
        return compile(sourceFile)
    }

    override fun compile(sourceFile: File): File {
        if (sourceFile.exists()) {
            return compile(sourceFile, outPutFile)
        }
        val sourceAddedPathFile = File(System.getProperty("user.dir"), sourceFile.path)
        if (sourceAddedPathFile.exists()) {
            return compile(sourceAddedPathFile, outPutFile)
        }
        error("Source file $sourceAddedPathFile does not exist!")
    }

    private fun compile(
        sourceFile: File,
        outPutFile: File,
    ): File {
        val compiler = K2JVMCompiler()

        val args =
            K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = outPutFile.absolutePath
                classpathAsList = listOf(File("dagger-dsl-core.jar"), File("build/libs/kotlin-stdlib-1.9.25.jar"))
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
