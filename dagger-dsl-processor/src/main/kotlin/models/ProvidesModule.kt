package models

data class ProvidesModule(
    val provides: List<Provides> = emptyList(),
) : Module {
    override val name: String
        get() {
            val firstProvidesType = provides.firstOrNull()?.type?.name ?: ""
            return "Module$firstProvidesType"
        }
}

data class Provides(
    val isSingleton: Boolean,
    val paramTypes: List<Type>,
    val type: Type,
    val body: String,
)
