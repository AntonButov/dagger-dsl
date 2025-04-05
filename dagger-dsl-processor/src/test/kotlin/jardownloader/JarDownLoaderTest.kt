package usecases.component.jardownloader

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteReadChannel
import io.mockk.every
import io.mockk.mockk
import jardownloader.JarDownloaderImpl
import jardownloader.SystemPropertiesProvider
import kotlinx.coroutines.runBlocking
import java.io.File

class JarDownLoaderTest : BehaviorSpec({

    Given("JarDownloader with MockEngine") {
        val kotlinVersion = "some version"
        val coreVersion = "some version"
        val systemPropertiesProvider =
            mockk<SystemPropertiesProvider> {
                every { getKotlinVersion() } returns kotlinVersion
                every { getCoreVersion() } returns coreVersion
            }
        val filesToCleanup = mutableListOf<File>()

        beforeSpec {
            File("dagger-dsl-core-$coreVersion.jar").delete()
            File("kotlin-stdlib-$kotlinVersion.jar").delete()
        }

        afterSpec {
            filesToCleanup.forEach { file ->
                if (file.exists()) {
                    file.delete()
                }
            }
        }

        val mockEngine =
            MockEngine { request ->
                respond(
                    content = ByteReadChannel(ByteArray(100)), // 100 байт произвольных данных
                    status = HttpStatusCode.OK,
                    headers = headersOf(HttpHeaders.ContentType, "application/java-archive"),
                )
            }

        val client = HttpClient(mockEngine)
        val jarDownloader =
            JarDownloaderImpl(
                logger = mockk(relaxed = true),
                client = client,
                systemPropertiesProvider = systemPropertiesProvider,
            )

        When("downLoadCore is called") {
            val result = runBlocking { jarDownloader.downloadCore(false) }

            Then("should return the file") {
                result.shouldBeTypeOf<File>()
                result.exists() shouldBe true
                result.name shouldBe "dagger-dsl-core-$coreVersion.jar"

                mockEngine.requestHistory.last().url.toString() shouldBe
                    "https://repo1.maven.org/maven2/io/github/antonbutov/dagger-dsl-core/$coreVersion/dagger-dsl-core-$coreVersion.jar"

                filesToCleanup.add(result)
            }
        }

        When("downLoadStdLib is called") {
            val result = runBlocking { jarDownloader.downloadStdLib(false) }

            Then("should return the file") {
                result.shouldBeTypeOf<File>()
                result.exists() shouldBe true
                result.name shouldBe "kotlin-stdlib-$kotlinVersion.jar"

                mockEngine.requestHistory.last().url.toString() shouldBe
                    "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$kotlinVersion/kotlin-stdlib-$kotlinVersion.jar"

                filesToCleanup.add(result)
            }
        }
    }
})
