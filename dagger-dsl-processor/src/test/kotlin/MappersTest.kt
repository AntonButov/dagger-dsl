package usescases.component

import ComponentToFileSpecMapper
import ComponentToFileSpecMapperImpl
import dagger.dsl.core.component
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class MappersTest : StringSpec({

    val mapper: ComponentToFileSpecMapper = ComponentToFileSpecMapperImpl()

    "default component" {
        val component = component {}

        val fileSpec = mapper.map(component)
        fileSpec.toString() shouldBe
            """
                |@dagger.Component
                |public interface component
                |
            """.trimMargin()
    }

    "simple named case" {
        val component =
            component {
                name = "myComponent"
            }

        val fileSpec = mapper.map(component)

        fileSpec.toString() shouldBe
            """
                |@dagger.Component
                |public interface myComponent
                |
            """.trimMargin()
    }
})
