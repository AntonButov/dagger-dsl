package usecases

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo

interface Writer {
    fun write(
        file: KSFile,
        daggerCode: TypeSpec,
    )
}

class WriterImpl(private val codeGenerator: CodeGenerator) : Writer {
    override fun write(
        file: KSFile,
        daggerCode: TypeSpec,
    ) {
        FileSpec.builder(
            packageName = file.packageName.asString(),
            fileName = file.fileName.split(".").first(),
        ).addType(daggerCode)
            .build()
            .writeTo(
                codeGenerator = codeGenerator,
                dependencies = Dependencies(aggregating = true, sources = arrayOf(file)),
            )
    }
}
