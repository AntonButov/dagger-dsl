package processor

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import methodmappers.MethodToComponentMapper
import transformers.ComponentToFileSpecMapper
import usecases.ComponentMethodFinder
import usecases.Writer
import usecases.findDsl

class Processor(
    private val logger: KSPLogger,
    private val componentToFileSpecMapper: ComponentToFileSpecMapper,
    private val writer: Writer,
    private val methodToComponentMapper: MethodToComponentMapper,
    private val componentMethodFinder: ComponentMethodFinder,
) : SymbolProcessor {
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (resolver.getNewFiles().toList().any { it.filePath.contains("generated") }) {
            return emptyList() // https://github.com/AntonButov/dagger-dsl/issues/22
        }

        val dslFun = resolver.findDsl() ?: return emptyList()
        val componentMethod = componentMethodFinder.mapComponent(dslFun)
        val component = methodToComponentMapper.mapToComponent(componentMethod, resolver)
        val specs = componentToFileSpecMapper.mapToSpecForWriter(component)
        writer.write(
            file = dslFun.containingFile,
            specs = specs,
        )
        return emptyList()
    }
}
