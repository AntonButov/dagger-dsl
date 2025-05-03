package usecases

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import dagger.dsl.core.DaggerDsl

@Throws(IllegalStateException::class)
fun Resolver.findDsl(): DaggerDslFunction? {
    val daggerDslFunctions =
        getAllFiles()
            .flatMap { it.declarations }
            .filter {
                it.annotations.any {
                    it.shortName.asString() == DaggerDsl::class.simpleName
                }
            }
            .map {
                DaggerDslFunction(
                    containingFile = it.containingFile ?: error("File not found."),
                    nameFun = it.simpleName.asString(),
                )
            }

    if (daggerDslFunctions.toList().size > 1) error("Multiple main functions found")
    return daggerDslFunctions.firstOrNull()
}

data class DaggerDslFunction(
    val containingFile: KSFile,
    val nameFun: String,
)
