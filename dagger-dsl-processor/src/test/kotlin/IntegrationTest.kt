
import dagger.dsl.core.Component
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.mockk.mockk
import jardownloader.JarDownloaderImpl
import jardownloader.SystemPropertiesProviderImpl
import usecases.ClassLoaderImpl
import usecases.CompilerImpl
import java.io.File

class IntegrationTest : StringSpec({

    "simple file dsl should to compile, then load and call method should return data dsl object" {
        val systemPropertiesProvider = SystemPropertiesProviderImpl()
        val okHttpClient = HttpClient(CIO)
        val jarDownloader = JarDownloaderImpl(mockk(relaxed = true), systemPropertiesProvider, okHttpClient)
        val compiler = CompilerImpl(jarDownloader, mockk(relaxed = true))
        val classLoader = ClassLoaderImpl()

        val classFile = compiler.compile(File("src/test/kotlin/test/dsl/SimpleCase.kt"))
        val result = classLoader.runStaticMethod(classFile, "test.dsl.SimpleCaseKt", "dsl") as Component

        result.name shouldBe "test"
    }
})
