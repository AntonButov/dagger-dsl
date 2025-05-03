package typeFinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type
import usecases.getClassKind

/**
 * Extension function to find a type by name and class kind in the resolver.
 *
 * @param name The simple name of the type to find
 * @param classType The kind of class to look for (e.g., CLASS, INTERFACE)
 * @return The found Type
 * @throws IllegalStateException if the type is not found
 */
fun Resolver.findTypeByName(
    name: String,
    classType: ClassKind,
): Type {
    val declaration =
        this
            .getClassKind(classType)
            .firstOrNull { it.simpleName.asString() == name }
            ?: error("Type not found by name: $name")

    return Type(
        name = declaration.simpleName.asString(),
        packageName = declaration.packageName.asString(),
    )
}
