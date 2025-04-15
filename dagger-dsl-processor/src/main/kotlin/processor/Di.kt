package processor

import ComponentToFileSpecMapper
import ComponentToFileSpecMapperImpl
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import jardownloader.JarDownloader
import jardownloader.JarDownloaderImpl
import jardownloader.SystemPropertiesProvider
import jardownloader.SystemPropertiesProviderImpl
import org.koin.dsl.module
import usecases.ClassLoader
import usecases.ClassLoaderImpl
import usecases.Compiler
import usecases.CompilerImpl
import usecases.Writer
import usecases.WriterImpl

val module =
    module {
        factory<Compiler> { CompilerImpl(get(), get()) }
        factory<ClassLoader> { ClassLoaderImpl() }
        factory<KSPLogger> { get<SymbolProcessorEnvironment>().logger }
        factory<SymbolProcessor> {
            Processor(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        factory<SystemPropertiesProvider> { SystemPropertiesProviderImpl() }
        factory<JarDownloader> { JarDownloaderImpl(get(), get(), get()) }
        factory<HttpClient> { HttpClient(CIO) }
        factory<ComponentToFileSpecMapper> { ComponentToFileSpecMapperImpl() }
        factory<CodeGenerator> { get<SymbolProcessorEnvironment>().codeGenerator }
        factory<Writer> { WriterImpl(get()) }
    }

fun environmentModule(environment: SymbolProcessorEnvironment) =
    module {
        factory<SymbolProcessorEnvironment> { environment }
    }
