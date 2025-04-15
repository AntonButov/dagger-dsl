package models

data class AbstractModule(
    val binds: List<Bind>,
) {
    val name: String
        get() {
            val firstBindType = binds.firstOrNull()?.bindTypes?.type?.name ?: ""
            return "Module$firstBindType"
        }
}

data class Bind(
    val isSingleton: Boolean,
    val bindTypes: BindTypes,
)

data class BindTypes(
    val type: Type,
    val impl: Type,
)

data class Type(
    val name: String,
    val packageName: String,
)
