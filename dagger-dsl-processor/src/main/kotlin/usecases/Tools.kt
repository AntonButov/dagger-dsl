package usecases

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSClassDeclaration

fun Resolver.getClassKind(classKind: ClassKind): Sequence<KSClassDeclaration> =
    getAllFiles().flatMap { file ->
        file.declarations.filterIsInstance<KSClassDeclaration>()
            .filter { it.classKind == classKind }
    }
