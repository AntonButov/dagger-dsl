import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import usecases.findDslMainFunction

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
            val result = resolver.findDslMainFunction()
            result!!.nameFun shouldBeEqual "anyNameFunction"
        }
    }
})
