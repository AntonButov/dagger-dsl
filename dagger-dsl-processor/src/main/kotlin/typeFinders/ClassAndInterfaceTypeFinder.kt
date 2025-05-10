package typeFinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type

interface ClassAndInterfaceTypeFinder {
    fun findByName(
        resolver: Resolver,
        name: String,
    ): Type
}

class ClassAndInterfaceTypeFinderImpl : ClassAndInterfaceTypeFinder {
    override fun findByName(
        resolver: Resolver,
        name: String,
    ): Type {
        val declaration =
            resolver.getDeclarations(ClassKind.CLASS, ClassKind.INTERFACE)
                .firstOrNull { it.simpleName.asString() == name } ?: error("Type not found by name: $name")
        return Type(
            name = declaration.simpleName.asString(),
            packageName = declaration.packageName.asString(),
        )
    }
}
