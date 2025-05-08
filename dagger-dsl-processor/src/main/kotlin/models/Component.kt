package models

data class Component(
    val isSingleton: Boolean,
    val name: String = "Component",
    val componentType: ComponentType,
    val abstractModules: List<AbstractModule>,
    val providesModules: List<ProvidesModule>,
)

data class ComponentType(
    val name: String,
    val methods: List<ComponentTypeMethod>,
)

data class ComponentTypeMethod(
    val name: String,
    val params: List<Param>,
    val returnType: Type?,
)

data class Param(
    val name: String,
    val type: Type,
)
