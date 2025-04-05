package usecases.component

import compile
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import usecases.findDaggerDslMainFunction

class FindDaggerDslMainFunctionTest : StringSpec({

    "component should be found" {
        val diFunction =
            """
            @DaggerDsl
            fun anyNameFunction() {
                component {
                }
            }
            
            fun otherFun() {
            
            }
            """.trimIndent()

        diFunction compile { resolver ->
            val result = resolver.findDaggerDslMainFunction()
            result!!.nameFun shouldBeEqual "anyNameFunction"
        }
    }
})
