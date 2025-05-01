package processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import org.koin.dsl.module
import transformers.ComponentToFileSpecMapper
import transformers.ComponentToFileSpecMapperImpl
import usecases.ComponentMethodFinder
import usecases.ComponentMethodFinderImpl
import usecases.ComponentTypeFinder
import usecases.ComponentTypeFinderImpl
import usecases.MethodToComponentMapper
import usecases.MethodToComponentMapperImpl
import usecases.Writer
import usecases.WriterImpl
import usecases.bindfinders.BindImplFinder
import usecases.bindfinders.BindImplFinderImpl
import usecases.bindfinders.BindTypeFinder
import usecases.bindfinders.BindTypeFinderImpl

val module =
    module {
        factory<KSPLogger> { get<SymbolProcessorEnvironment>().logger }
        factory<CodeGenerator> { get<SymbolProcessorEnvironment>().codeGenerator }
        factory<SymbolProcessor> {
            Processor(
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }
        factory<ComponentToFileSpecMapper> { ComponentToFileSpecMapperImpl() }
        factory<Writer> { WriterImpl(get()) }
        factory<MethodToComponentMapper> { MethodToComponentMapperImpl(get(), get(), get()) }
        factory<ComponentTypeFinder> { ComponentTypeFinderImpl() }
        factory<ComponentMethodFinder> { ComponentMethodFinderImpl() }
        factory<BindTypeFinder> { BindTypeFinderImpl() }
        factory<BindImplFinder> { BindImplFinderImpl() }
    }

fun environmentModule(environment: SymbolProcessorEnvironment) =
    module {
        factory<SymbolProcessorEnvironment> { environment }
    }
