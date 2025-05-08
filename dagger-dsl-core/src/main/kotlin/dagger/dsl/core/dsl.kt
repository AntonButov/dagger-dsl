package dagger.dsl.core

fun <T : Any> component(dsl: ComponentBuilder.() -> Unit) {
}

fun <T : Any> componentSingleton(dsl: ComponentBuilderSingleton.() -> Unit) {
}

interface ComponentBuilder : Binder, Provider {
    fun moduleAbstract(dsl: AbstractModuleDsl.() -> Unit)

    fun module(dsl: ModuleDsl.() -> Unit)
}

interface ComponentBuilderSingleton : ComponentBuilder, BinderSingleton, ProviderSingleton

interface AbstractModuleDsl : BinderSingleton

interface BinderSingleton : Binder {
    fun <T, I : Any> bindSingleton()
}

interface ModuleDsl : ProviderSingleton

interface ProviderSingleton : Provider {
    fun <T : Any> providesSingleton(factory: () -> T)
}

interface Binder {
    fun <T, I : Any> bind()
}

interface Provider {
    fun <T : Any> provides(factory: () -> T)
}

fun <T : Any> get(): T {
    throw UnsupportedOperationException("This method is only used for building the DSL structure.")
}
