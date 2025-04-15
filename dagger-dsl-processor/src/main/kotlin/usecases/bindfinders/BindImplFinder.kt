package usecases.bindfinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type
import usecases.getClassKind

interface BindImplFinder {
    fun findByName(
        resolver: Resolver,
        name: String,
    ): Type
}

class BindImplFinderImpl : BindImplFinder {
    override fun findByName(
        resolver: Resolver,
        name: String,
    ): Type {
        val implDeclaration = resolver.getClassKind(ClassKind.CLASS).first { it.simpleName.asString() == name }
        return Type(
            name = implDeclaration.simpleName.asString(),
            packageName = implDeclaration.packageName.asString(),
        )
    }
}
