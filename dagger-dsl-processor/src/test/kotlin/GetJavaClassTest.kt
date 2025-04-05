package usecases

import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class FindComponentTest : BehaviorSpec({

    given("a KSFunctionDeclaration with a file") {
        `when`("file name is 'dagger.kt'") {
            val mockFile = mockk<KSFile>()
            every { mockFile.fileName } returns "dagger.kt"

            val mockFunction = mockk<KSFunctionDeclaration>()
            every { mockFunction.containingFile } returns mockFile

            then("Java class name should be 'DaggerKt'") {
                mockFunction.getJavaClassName() shouldBe "DaggerKt"
            }
        }

        `when`("file name is already capitalized 'Dagger.kt'") {
            val mockFile = mockk<KSFile>()
            every { mockFile.fileName } returns "Dagger.kt"

            val mockFunction = mockk<KSFunctionDeclaration>()
            every { mockFunction.containingFile } returns mockFile

            then("Java class name should be 'DaggerKt'") {
                mockFunction.getJavaClassName() shouldBe "DaggerKt"
            }
        }

        `when`("file name has multiple dots 'my.complex.dagger.kt'") {
            val mockFile = mockk<KSFile>()
            every { mockFile.fileName } returns "my.complex.dagger.kt"

            val mockFunction = mockk<KSFunctionDeclaration>()
            every { mockFunction.containingFile } returns mockFile

            then("Java class name should be 'MyKt'") {
                mockFunction.getJavaClassName() shouldBe "MyKt"
            }
        }
    }

    given("a KSFunctionDeclaration with null file") {
        val mockFunction = mockk<KSFunctionDeclaration>()
        every { mockFunction.containingFile } returns null

        then("it should throw IllegalStateException with message 'File not found.'") {
            val exception =
                shouldThrow<IllegalStateException> {
                    mockFunction.getJavaClassName()
                }
            exception.message shouldBe "File not found."
        }
    }
})
