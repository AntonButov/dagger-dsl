package usecases

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import dagger.dsl.core.DaggerDsl

@Throws(IllegalStateException::class)
fun Resolver.findDaggerDslMainFunction(): DaggerDslFunction? {
    val daggerDslFunctions =
        getAllFiles()
            .flatMap { it.declarations }
            .filter {
                it.annotations.any {
                    it.shortName.asString() == DaggerDsl::class.simpleName
                }
            }.filterIsInstance<KSFunctionDeclaration>()
            .map {
                DaggerDslFunction(
                    containingFile = it.containingFile ?: error("File not found."),
                    className = it.getJavaClassName(),
                    nameFun = it.simpleName.asString(),
                )
            }

    if (daggerDslFunctions.toList().size > 1) error("Multiple main functions found")
    return daggerDslFunctions.firstOrNull()
}

fun KSFunctionDeclaration.getJavaClassName(): String {
    val containingFile = containingFile ?: error("File not found.")
    val fileName = containingFile.fileName

    return fileName.replaceFirstChar { it.uppercase() }.split(".").first() + "Kt"
}

data class DaggerDslFunction(
    val containingFile: KSFile,
    val className: String,
    val nameFun: String,
)
