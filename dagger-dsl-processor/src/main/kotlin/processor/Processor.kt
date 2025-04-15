package processor

import ComponentToFileSpecMapper
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import dagger.dsl.core.Component
import kotlinx.coroutines.runBlocking
import usecases.ClassLoader
import usecases.Compiler
import usecases.Writer
import usecases.findDaggerDslMainFunction
import java.io.File

class Processor(
    private val compiler: Compiler,
    private val classLoader: ClassLoader,
    private val logger: KSPLogger,
    private val componentToFileSpecMapper: ComponentToFileSpecMapper,
    private val writer: Writer,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val dslFun = resolver.findDaggerDslMainFunction() ?: return emptyList()
        if (resolver.getNewFiles().toList().any { it.filePath.contains("generated") }) {
            return emptyList() // https://github.com/AntonButov/dagger-dsl/issues/22
        }
        runBlocking {
            val jarFile: File =
                compiler.compile(sourceFile = File(dslFun.containingFile.filePath))
            val component = classLoader.runStaticMethod(jarFile, dslFun.className, dslFun.nameFun) as Component
            val daggetCode = componentToFileSpecMapper.map(component)
            writer.write(
                file = dslFun.containingFile,
                daggerCode = daggetCode,
            )
        }
        return emptyList()
    }
}
