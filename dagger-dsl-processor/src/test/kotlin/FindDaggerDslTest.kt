import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equals.shouldBeEqual
import usecases.findDsl

class FindDaggerDslTest : StringSpec({

    "fun component should be found" {
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
            val result = resolver.findDsl()
            result!!.nameFun shouldBeEqual "anyNameFunction"
        }
    }

    "val component should be found" {
        val diVal =
            """
            @DaggerDsl
            val component = component {
                }
            """.trimIndent()

        diVal compile { resolver ->
            val result = resolver.findDsl()
            result!!.nameFun shouldBeEqual "component"
        }
    }
})
