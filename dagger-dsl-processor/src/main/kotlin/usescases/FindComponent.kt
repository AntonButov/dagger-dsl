package usescases

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSDeclaration
import dagger.dsl.core.DaggerDsl

@Throws(IllegalStateException::class)
fun Resolver.findDaggerDslMainFunction(): KSDeclaration {
    val daggerDslFunctions =
        getAllFiles().map { it.declarations }.flatten()
            .filter {
                it.annotations.any {
                    it.shortName.asString() == DaggerDsl::class.simpleName
                }
            }

    if (daggerDslFunctions.toList().size > 1) error("Multiple main functions found")
    return daggerDslFunctions.firstOrNull() ?: error("No main function found")
}
