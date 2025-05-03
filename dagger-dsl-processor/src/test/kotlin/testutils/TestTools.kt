package testutils

import java.io.File

object TestFileCreator {
    fun createTestFile(code: String): File {
        val tempFile = File.createTempFile("test", ".kt")
        tempFile.deleteOnExit()
        tempFile.writeText(code)
        return tempFile
    }
}
