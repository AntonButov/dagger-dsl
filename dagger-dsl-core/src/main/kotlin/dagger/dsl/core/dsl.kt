package dagger.dsl.core

/**
 * Creates a component using the provided DSL.
 * This method is intentionally empty as it's only used for building the DSL structure.
 * The actual implementation is provided by the annotation processor.
 */
fun <T : Any> component(dsl: ComponentBuilder.() -> Unit) {
}

/**
 * Creates a singleton component using the provided DSL.
 * This method is intentionally empty as it's only used for building the DSL structure.
 * The actual implementation is provided by the annotation processor.
 */
fun <T : Any> componentSingleton(dsl: ComponentBuilder.() -> Unit) {
}

/**
 * Builder class for component configuration.
 * Methods in this class are intentionally empty as they're only used for building the DSL structure.
 * The actual implementation is provided by the annotation processor.
 */
class ComponentBuilder() {
    /**
     * Adds an abstract module to the component.
     * This method is intentionally empty as it's only used for building the DSL structure.
     */
    fun moduleAbstract(dsl: AbstractModuleDsl.() -> Unit) {
    }

    /**
     * Adds a module to the component.
     * This method is intentionally empty as it's only used for building the DSL structure.
     */
    fun module(dsl: ModuleDsl.() -> Unit) {
    }
}

/**
 * DSL for configuring abstract modules.
 * Methods in this class are intentionally empty as they're only used for building the DSL structure.
 * The actual implementation is provided by the annotation processor.
 */
class AbstractModuleDsl() {
    /**
     * Binds an implementation to an interface.
     * This method is intentionally empty as it's only used for building the DSL structure.
     */
    fun <T, I : Any> bind() {
    }

    /**
     * Binds an implementation to an interface as a singleton.
     * This method is intentionally empty as it's only used for building the DSL structure.
     */
    fun <T, I : Any> bindSingleton() {
    }
}

/**
 * DSL for configuring modules.
 * Methods in this class are intentionally empty as they're only used for building the DSL structure.
 * The actual implementation is provided by the annotation processor.
 */
class ModuleDsl() {
    /**
     * Provides an implementation for a type.
     * This method is intentionally empty as it's only used for building the DSL structure.
     */
    fun <T, I : Any> provides() {
    }
}
