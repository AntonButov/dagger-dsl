package usecases

import com.google.devtools.ksp.processing.KSPLogger
import jardownloader.JarDownloader
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import java.io.File
import java.io.IOException

interface Compiler {
    @Throws(IOException::class)
    suspend fun compile(sourceCode: String): File
    @Throws(IOException::class)
    suspend fun compile(sourceFile: File): File
}

private const val OUTPUT_DIR_NAME = "build/tmp"

class CompilerImpl(
    private val jarDownloader: JarDownloader,
    private val logger: KSPLogger,
) : Compiler {
    private val outPutFile by lazy { File(OUTPUT_DIR_NAME).apply { mkdir() } }
    @Throws(IOException::class)
    override suspend fun compile(sourceCode: String): File {
        val sourceFile = File(outPutFile, "Generated.kt").apply { writeText(sourceCode) }
        return compile(sourceFile)
    }
    @Throws(IOException::class)
    override suspend fun compile(sourceFile: File): File {
        if (sourceFile.exists()) {
            return compile(sourceFile, outPutFile)
        }
        val sourceAddedPathFile = File(System.getProperty("user.dir"), sourceFile.path)
        if (sourceAddedPathFile.exists()) {
            return compile(sourceAddedPathFile, outPutFile)
        }
        error("Source file $sourceAddedPathFile does not exist!")
    }
    @Throws(IOException::class)
    private suspend fun compile(
        sourceFile: File,
        outPutFile: File,
    ): File {
        val compiler = K2JVMCompiler()

        val daggerDslCoreJar = jarDownloader.downloadCore()
        val stdLibJar = jarDownloader.downloadStdLib()

        val args =
            K2JVMCompilerArguments().apply {
                freeArgs = listOf(sourceFile.absolutePath)
                destination = outPutFile.absolutePath
                classpathAsList = listOf(daggerDslCoreJar, stdLibJar)
            }

        val compilerMessageCollector =
            PrintingMessageCollector(
                System.out,
                MessageRenderer.WITHOUT_PATHS,
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
