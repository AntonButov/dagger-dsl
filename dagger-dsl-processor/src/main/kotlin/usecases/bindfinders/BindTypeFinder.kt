package usecases.bindfinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type
import usecases.getClassKind

interface BindTypeFinder {
    fun findByName(
        resolver: Resolver,
        name: String,
    ): Type
}

class BindTypeFinderImpl : BindTypeFinder {
    override fun findByName(
        resolver: Resolver,
        name: String,
    ): Type {
        val typeDeclaration =
            resolver
                .getClassKind(ClassKind.INTERFACE)
                .firstOrNull { it.simpleName.asString() == name } ?: error("Type not found by name: $name")
        return Type(
            name = typeDeclaration.simpleName.asString(),
            packageName = typeDeclaration.packageName.asString(),
        )
    }
}
