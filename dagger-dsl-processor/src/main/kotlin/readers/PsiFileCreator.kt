package readers

import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.com.intellij.openapi.util.Disposer
import org.jetbrains.kotlin.com.intellij.psi.PsiManager
import org.jetbrains.kotlin.com.intellij.testFramework.LightVirtualFile
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.idea.KotlinLanguage
import org.jetbrains.kotlin.psi.KtFile
import java.io.File

interface PsiFileCreator {
    fun createKtFile(
        code: String,
        fileName: String = "Sample.kt",
    ): KtFile

    fun createKtFileFromRealFile(filePath: String): KtFile {
        val file = File(filePath)
        val fileName = file.name
        val content = file.readText()
        return createKtFile(content, fileName)
    }
}

class PsiFileCreatorImpl : PsiFileCreator {
    override fun createKtFile(
        code: String,
        fileName: String,
    ): KtFile {
        val disposable = Disposer.newDisposable()
        val configuration = CompilerConfiguration()
        val environment =
            KotlinCoreEnvironment.Companion.createForProduction(
                disposable,
                configuration,
                EnvironmentConfigFiles.JVM_CONFIG_FILES,
            )
        val project = environment.project
        val virtualFile = LightVirtualFile(fileName, KotlinLanguage.INSTANCE, code)
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: error("Psi file not found")
        return psiFile as KtFile
    }
}
