package usescases

import dagger.dsl.core.DaggerDsl
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFunctionDeclaration

@Throws(IllegalStateException::class)
fun Resolver.findDaggerDslMainFunction(): KSFunctionDeclaration {
    val daggerDslFunctions =
        getSymbolsWithAnnotation(DaggerDsl::class.java.name)
            .filterIsInstance<KSFunctionDeclaration>()

    if (daggerDslFunctions.toList().size > 1) error("Multiple main functions found")
    return daggerDslFunctions.firstOrNull() ?: error("No main function found")
}
