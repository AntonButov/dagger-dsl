package jardownloader

import com.google.devtools.ksp.processing.KSPLogger
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File
import java.io.IOException

interface JarDownloader {
    val client: HttpClient

    @Throws(IOException::class)
    suspend fun downloadCore(fromCach: Boolean = true): File

    @Throws(IOException::class)
    suspend fun downloadStdLib(fromCache: Boolean = true): File
}

class JarDownloaderImpl(
    private val logger: KSPLogger,
    private val systemPropertiesProvider: SystemPropertiesProvider,
    override val client: HttpClient,
) : JarDownloader {
    private val coreVersion
        get() = systemPropertiesProvider.getCoreVersion()
    private val kotlinVersion
        get() = systemPropertiesProvider.getKotlinVersion()
    private val downloadDir =
        File("build/tmp/dagger-dsl").apply {
            if (!exists()) {
                mkdirs()
            }
        }

    override suspend fun downloadCore(fromCache: Boolean): File {
        logger.warn("core version $coreVersion")
        val file = File(downloadDir, "dagger-dsl-core-$coreVersion.jar")
        if (fromCache && file.exists()) {
            return file
        }
        logger.info("Downloading core version $coreVersion")
        val requestUrl = "https://repo1.maven.org/maven2/io/github/antonbutov/dagger-dsl-core/$coreVersion/dagger-dsl-core-$coreVersion.jar"
        val bytes: ByteArray = client.get(requestUrl).readBytes()
        return file.apply { writeBytes(bytes) }
    }

    override suspend fun downloadStdLib(isOver: Boolean): File {
        val file = File(downloadDir, "kotlin-stdlib-$kotlinVersion.jar")
        if (isOver && file.exists()) {
            return file
        }
        logger.info("Downloading kotlin-stdlib version $kotlinVersion")
        val requestUrl = "https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-stdlib/$kotlinVersion/kotlin-stdlib-$kotlinVersion.jar"
        val bytes = client.get(requestUrl).readBytes()
        return file.apply { writeBytes(bytes) }
    }
}
