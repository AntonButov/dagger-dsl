package processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk

class ProcessorProviderTest : BehaviorSpec({

    Given("a ProcessorProvider") {
        val provider = ProcessorProvider()
        val environment = mockk<SymbolProcessorEnvironment>(relaxed = true)
        every {
            environment.logger
        } returns mockk()

        When("a SymbolProcessorEnvironment is provided") {
            val processor = provider.create(environment)

            Then("it should create a DaggerSymbolProcessor") {
                processor.shouldBeInstanceOf<SymbolProcessor>()
            }
        }

        When("create is called multiple times with the same environment") {
            val processor1 = provider.create(environment)
            val processor2 = provider.create(environment)

            Then("it should create a new instance each time") {
                processor1 shouldNotBe processor2
            }
        }
    }
})
