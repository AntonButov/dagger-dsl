package transformers

import com.squareup.kotlinpoet.KModifier
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import models.AbstractModule
import models.Bind
import models.BindTypes
import models.Type

class AbstractModuleToTypeSpecMapperTest : BehaviorSpec({
    Given("An AbstractModuleToTypeSpecMapper") {
        val mapper = AbstractModuleToTypeSpecMapperImpl()

        When("Mapping an empty list of modules") {
            val result = mapper.map(emptyList())

            Then("Should return an empty list") {
                result shouldHaveSize 0
            }
        }

        When("Mapping a module with a single non-singleton bind") {
            val bindType = Type("TestInterface", "com.example")
            val implType = Type("TestImpl", "com.example")
            val bind =
                Bind(
                    isSingleton = false,
                    bindTypes =
                        BindTypes(
                            type = bindType,
                            impl = implType,
                        ),
                )
            val module =
                AbstractModule(
                    binds = listOf(bind),
                )

            val result = mapper.map(listOf(module))

            Then("Should generate a module class with the correct name") {
                result shouldHaveSize 1
                val typeSpec = result[0]
                typeSpec.name shouldBe "ModuleTestInterface"
            }

            Then("Should generate an abstract class with @Module annotation") {
                val typeSpec = result[0]
                typeSpec.modifiers shouldBe setOf(KModifier.ABSTRACT)
                typeSpec.annotations shouldHaveSize 1
                typeSpec.annotations[0].className.simpleName shouldBe "Module"
            }

            Then("Should generate a bind function without @Singleton") {
                val typeSpec = result[0]
                typeSpec.funSpecs shouldHaveSize 1

                val bindFunction = typeSpec.funSpecs[0]
                bindFunction.name shouldBe "bindTestInterface"
                bindFunction.modifiers shouldBe setOf(KModifier.ABSTRACT)
                bindFunction.annotations.any { it.className.simpleName == "Binds" } shouldBe true
                bindFunction.annotations.any { it.className.simpleName == "Singleton" } shouldBe false
            }
        }

        When("Mapping a module with a singleton bind") {
            val bindType = Type("TestInterface", "com.example")
            val implType = Type("TestImpl", "com.example")
            val bind =
                Bind(
                    isSingleton = true,
                    bindTypes =
                        BindTypes(
                            type = bindType,
                            impl = implType,
                        ),
                )
            val module =
                AbstractModule(
                    binds = listOf(bind),
                )

            val result = mapper.map(listOf(module))

            Then("Should generate a bind function with @Singleton") {
                val typeSpec = result[0]
                val bindFunction = typeSpec.funSpecs[0]
                bindFunction.annotations.any { it.className.simpleName == "Singleton" } shouldBe true
            }
        }

        When("Mapping multiple modules") {
            val bind1 =
                Bind(
                    isSingleton = false,
                    bindTypes =
                        BindTypes(
                            type = Type("Interface1", "com.example"),
                            impl = Type("Impl1", "com.example"),
                        ),
                )
            val module1 =
                AbstractModule(
                    binds = listOf(bind1),
                )

            val bind2 =
                Bind(
                    isSingleton = true,
                    bindTypes =
                        BindTypes(
                            type = Type("Interface2", "com.example"),
                            impl = Type("Impl2", "com.example"),
                        ),
                )
            val module2 =
                AbstractModule(
                    binds = listOf(bind2),
                )

            val result = mapper.map(listOf(module1, module2))

            Then("Should generate multiple module classes") {
                result shouldHaveSize 2
                result[0].name shouldBe "ModuleInterface1"
                result[1].name shouldBe "ModuleInterface2"
            }
        }

        When("Mapping a module with multiple binds") {
            val bind1 =
                Bind(
                    isSingleton = false,
                    bindTypes =
                        BindTypes(
                            type = Type("Interface1", "com.example"),
                            impl = Type("Impl1", "com.example"),
                        ),
                )
            val bind2 =
                Bind(
                    isSingleton = true,
                    bindTypes =
                        BindTypes(
                            type = Type("Interface2", "com.example"),
                            impl = Type("Impl2", "com.example"),
                        ),
                )
            val module =
                AbstractModule(
                    binds = listOf(bind1, bind2),
                )

            val result = mapper.map(listOf(module))

            Then("Should generate a module with multiple bind functions") {
                val typeSpec = result[0]
                typeSpec.funSpecs shouldHaveSize 2

                val bindFunction1 = typeSpec.funSpecs[0]
                bindFunction1.name shouldBe "bindInterface1"
                bindFunction1.annotations.any { "Singleton" in it.typeName.toString() } shouldBe false

                val bindFunction2 = typeSpec.funSpecs[1]
                bindFunction2.name shouldBe "bindInterface2"
                bindFunction2.annotations.any { "Singleton" in it.typeName.toString() } shouldBe true
            }
        }
    }
})
