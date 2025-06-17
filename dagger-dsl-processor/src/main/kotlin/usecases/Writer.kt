package usecases

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import transformers.SpecsForWriter

interface Writer {
    fun write(
        file: KSFile,
        specs: SpecsForWriter,
    )
}

class WriterImpl(private val codeGenerator: CodeGenerator) : Writer {
    override fun write(
        file: KSFile,
        specs: SpecsForWriter,
    ) {
        writeComponent(codeGenerator, specs.componentSpec, file)
        specs.moduleSpec.forEach {
            writeModule(
                codeGenerator = codeGenerator,
                spec = it,
                file = file,
            )
        }
    }

    private fun writeModule(
        codeGenerator: CodeGenerator,
        spec: TypeSpec,
        file: KSFile,
    ) {
        FileSpec.builder(
            packageName = file.packageName.asString(),
            fileName = spec.name.toString(),
        ).addType(spec)
            .build()
            .writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies.ALL_FILES,
            )
    }

    private fun writeComponent(
        codeGenerator: CodeGenerator,
        spec: TypeSpec,
        file: KSFile,
    ) {
        FileSpec.builder(
            packageName = file.packageName.asString(),
            fileName = file.fileName.split(".").first(),
        ).addType(spec)
            .build()
            .writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies.ALL_FILES,
            )
    }
}
