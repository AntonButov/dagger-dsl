package methodmappers

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import models.Provides
import models.Type
import psiUtils.Method
import typeFinders.ClassAndInterfaceTypeFinder

class ModuleMapperTest : BehaviorSpec({
    Given("A ModuleMapper with mocked dependencies") {
        val resolver = mockk<Resolver>()
        val type = Type("SomeClass", "SomeClassPackage")
        val typeFinder =
            mockk<ClassAndInterfaceTypeFinder> {
                every { findByName(any(), any()) } returns type
            }
        val moduleMapper: ModuleMapper = ModuleMapperImpl(typeFinder)

        When("Mapping a provides method") {
            val provide =
                Method(
                    name = "provides",
                    genericTypes = listOf("SomeClass"),
                    lambdaBody = "{ SomeClassImpl() }",
                )
            Then("Should create a provides entry") {
                val module = moduleMapper.mapModule(listOf(provide), resolver)
                module.provides shouldBe
                    listOf(
                        Provides(
                            isSingleton = false,
                            paramTypes = listOf(),
                            type = type,
                            body = "{ SomeClassImpl() }",
                        ),
                    )
            }
        }

        When("Mapping multiple provides methods") {
            val provides =
                listOf(
                    Method(
                        name = "provides",
                        genericTypes = listOf("SomeClass"),
                        lambdaBody = "{ SomeClassImpl() }",
                    ),
                    Method(
                        name = "provides",
                        genericTypes = listOf("AnotherClass"),
                        lambdaBody = "{ AnotherClassImpl() }",
                    ),
                )

            Then("Should create multiple provides entries") {
                val module = moduleMapper.mapModule(provides, resolver)
                module.provides.size shouldBe 2
                module.provides[0].body shouldBe "{ SomeClassImpl() }"
                module.provides[1].body shouldBe "{ AnotherClassImpl() }"
            }
        }

        When("Mapping a provides method with parameters") {
            val provides =
                Method(
                    name = "provides",
                    genericTypes = listOf("SomeClass"),
                    lambdaBody = "{ SomeClass(get<Dependency>()) }",
                )
            Then("Should create a provides entry with parameters") {
                val module = moduleMapper.mapModule(listOf(provides), resolver)

                module.provides.size shouldBe 1
                module.provides.first().isSingleton shouldBe false
                module.provides.first().type shouldBe type
                module.provides.first().body shouldBe "{ SomeClass(get<Dependency>()) }"
            }
        }
    }
})
