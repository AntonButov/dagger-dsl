package typeFinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import models.Type

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
        return resolver.findTypeByName(name, ClassKind.INTERFACE)
    }
}
