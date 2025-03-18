package usescases.component

import compile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import usescases.findDaggerDslMainFunction

class FindDaggerDslMainFunctionTest : StringSpec({

    "component should be found" {
        val diFunction =
            """
            @DaggerDsl    
            fun anyNameFunction() {
                component()
            }
            """.trimIndent()

        diFunction compile { resolver ->
            val result = resolver.findDaggerDslMainFunction()
            result.toString() shouldBeEqual "anyNameFunction"
        }
    }
})
