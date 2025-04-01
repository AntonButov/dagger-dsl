package dagger.dsl.core

fun component(dsl: ComponentBuilder.() -> Unit): Component {
    return ComponentBuilder().apply(dsl).build()
}

class ComponentBuilder {
    private var isSingleton = false
    var name: String = "component"
    private var modules: MutableList<Module> = mutableListOf()

    /**
     * Marks the component as a singleton.
     *
     * This ensures that only one instance of the component is created.
     */
    fun singleton() {
        isSingleton = true
    }

    fun module(dsl: ModuleDsl.() -> Unit) {
        modules.add(ModuleDsl().apply(dsl).build())
    }

    internal fun build() = Component(isSingleton, name, modules)
}

data class Component(
    val isSingleton: Boolean,
    val name: String,
    val modules: List<Module>,
)

class ModuleDsl() {
    private var name: String? = null
    val binds: MutableList<Pair<Class<*>, Class<*>>> = mutableListOf()

    inline fun <reified T> bind(classImpl: Class<out T>) {
        binds.add(T::class.java to classImpl)
    }

    fun build() = Module(name, binds)
}

data class Module(
    val name: String?,
    val binds: List<Pair<Class<*>, Class<*>>>,
)
