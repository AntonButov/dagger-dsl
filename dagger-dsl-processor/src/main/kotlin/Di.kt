import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import methodmappers.AbstractModuleMapper
import methodmappers.AbstractModuleMapperImpl
import methodmappers.MethodToComponentMapper
import methodmappers.MethodToComponentMapperImpl
import methodmappers.ModuleMapper
import methodmappers.ModuleMapperImpl
import org.koin.dsl.module
import processor.Processor
import transformers.AbstractModuleToTypeSpecMapper
import transformers.AbstractModuleToTypeSpecMapperImpl
import transformers.ComponentToFileSpecMapper
import transformers.ComponentToFileSpecMapperImpl
import transformers.ModuleToTypeSpecMapper
import transformers.ModuleToTypeSpecMapperImpl
import typeFinders.BindImplFinder
import typeFinders.BindImplFinderImpl
import typeFinders.BindTypeFinder
import typeFinders.BindTypeFinderImpl
import typeFinders.ClassAndInterfaceTypeFinder
import typeFinders.ClassAndInterfaceTypeFinderImpl
import typeFinders.ComponentTypeFinder
import typeFinders.ComponentTypeFinderImpl
import usecases.ComponentMethodFinder
import usecases.ComponentMethodFinderImpl
import usecases.Writer
import usecases.WriterImpl

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
        factory<ClassAndInterfaceTypeFinder> { ClassAndInterfaceTypeFinderImpl() }
        factory<AbstractModuleToTypeSpecMapper> { AbstractModuleToTypeSpecMapperImpl() }
        factory<ModuleToTypeSpecMapper> { ModuleToTypeSpecMapperImpl() }
        factory<ComponentToFileSpecMapper> { ComponentToFileSpecMapperImpl(get(), get()) }
        factory<Writer> { WriterImpl(get()) }
        factory<AbstractModuleMapper> { AbstractModuleMapperImpl(get(), get()) }
        single<ModuleMapper> { ModuleMapperImpl(get()) }
        factory<MethodToComponentMapper> {
            MethodToComponentMapperImpl(get(), get(), get())
        }
        factory<ComponentTypeFinder> { ComponentTypeFinderImpl() }
        factory<ComponentMethodFinder> { ComponentMethodFinderImpl() }
        factory<BindTypeFinder> { BindTypeFinderImpl() }
        factory<BindImplFinder> { BindImplFinderImpl() }
    }

fun environmentModule(environment: SymbolProcessorEnvironment) =
    module {
        factory<SymbolProcessorEnvironment> { environment }
    }
