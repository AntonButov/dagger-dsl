package typeFinders

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun Resolver.getDeclarations(vararg classKinds: ClassKind): Sequence<KSClassDeclaration> =
    getAllFiles().flatMap { file ->
        file.declarations.filterIsInstance<KSClassDeclaration>()
            .filter { dec -> classKinds.any { it == dec.classKind } }
    }
