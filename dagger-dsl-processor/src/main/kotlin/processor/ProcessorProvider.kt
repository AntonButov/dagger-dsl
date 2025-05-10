package processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import di.environmentModule
import di.module
import org.koin.core.context.GlobalContext.startKoin

class ProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        koin.loadModules(
            listOf(environmentModule(environment)),
        )
        return koin.get<SymbolProcessor>()
    }

    companion object {
        private val koin =
            startKoin {
                modules(module)
            }.koin
    }
}
