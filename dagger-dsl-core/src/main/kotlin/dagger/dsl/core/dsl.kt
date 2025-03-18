package dagger.dsl.core

fun component(dsl: Component.() -> Unit): Component {
    return Component().apply(dsl)
}

class Component {

    private var isSingleton = false
    private var name: String? = null
    private var modules: List<Module> = mutableListOf()
    fun singleton() {
        isSingleton = true
    }

}


class Module(val name: String) {

    val binds: MutableList<Pair<Class<*>, Class<*>>> = mutableListOf()

    inline fun <reified T> binds(classImpl: Class<out T>) {
        binds.add(T::class.java to classImpl)
    }
}