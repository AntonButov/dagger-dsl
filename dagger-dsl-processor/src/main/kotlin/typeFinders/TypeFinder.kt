package typeFinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type

fun Resolver.findTypeByName(
    name: String,
    classType: ClassKind,
): Type {
    val declaration =
        this
            .getDeclarations(classType)
            .firstOrNull { it.simpleName.asString() == name }
            ?: error("Type not found by name: $name")

    return Type(
        name = declaration.simpleName.asString(),
        packageName = declaration.packageName.asString(),
    )
}
