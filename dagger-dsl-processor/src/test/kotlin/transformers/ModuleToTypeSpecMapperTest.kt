package transformers

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import models.Provides
import models.ProvidesModule
import models.Type

class ModuleToTypeSpecMapperTest : BehaviorSpec({
    Given("A ModuleToTypeSpecMapper") {
        val mapper: ModuleToTypeSpecMapper =
            ModuleToTypeSpecMapperImpl()
        When("Mapping an empty list of modules") {
            val result = mapper.map(emptyList())

            Then("Should return an empty list") {
                result shouldHaveSize 0
            }
        }
        val coffeeType = Type("Coffee", "com.coffee")
        val coffeeType1 = Type("Coffee1", "com.example")
        val coffeeType2 = Type("Coffee2", "com.example")

        When("Mapping a module with a single non-singleton provides") {

            val provides =
                Provides(
                    isSingleton = false,
                    paramTypes = emptyList(),
                    type = coffeeType,
                    body =
                        """
                        { Coffee() }
                        """.trimIndent(),
                )
            val providesModule = ProvidesModule(provides = listOf(provides))

            val result = mapper.map(listOf(providesModule))

            Then("Should generate a module class with the correct name") {
                result shouldHaveSize 1
                val typeSpec = result.first()
                typeSpec.name shouldBe "ModuleCoffee"
            }

            Then("Should generate a class with @Module annotation") {
                val typeSpec = result.first()
                typeSpec.modifiers shouldBe emptySet()
                typeSpec.annotations shouldHaveSize 1
                typeSpec.annotations.first().typeName.toString() shouldContain "Module"
            }

            Then("Should generate a provides function without @Singleton") {
                val typeSpec = result.first()
                typeSpec.funSpecs shouldHaveSize 1

                val provideFunction = typeSpec.funSpecs.first()
                provideFunction.name shouldBe "providesCoffee"
                provideFunction.annotations.any { "Provides" in it.typeName.toString() } shouldBe true
                provideFunction.annotations.any { "Singleton" in it.typeName.toString() } shouldBe false
            }
        }

        When("Mapping a module with a singleton provides") {
            val provides =
                Provides(
                    isSingleton = true,
                    paramTypes = emptyList(),
                    type = coffeeType,
                    body =
                        """
                        { Coffee() }
                        """.trimIndent(),
                )
            val providesModule =
                ProvidesModule(
                    provides = listOf(provides),
                )

            val result = mapper.map(listOf(providesModule))

            Then("Should generate a provides function with @Singleton") {
                val typeSpec = result.first()
                val providesFunction = typeSpec.funSpecs.first()
                providesFunction.annotations.any { it.className.simpleName == "Singleton" } shouldBe true
            }
        }

        When("Mapping a simple case have not to params in provides method") {
            val provides =
                Provides(
                    isSingleton = false,
                    paramTypes = emptyList(),
                    type = coffeeType,
                    body =
                        """
                        { Coffee() }
                        """.trimIndent(),
                )
            val providesModule =
                ProvidesModule(
                    provides = listOf(provides),
                )

            val result = mapper.map(listOf(providesModule))

            Then("Params should be empty") {
                val typeSpec = result.first()
                val providesFunction = typeSpec.funSpecs.first()
                providesFunction.parameters shouldBe emptyList()
            }

            Then("Return type should be correct") {
                val typeSpec = result.first()
                val providesFunction = typeSpec.funSpecs.first()
                providesFunction.returnType.toString() shouldBe "com.coffee.Coffee"
            }
        }

        When("Mapping a case with param have to params in provides method") {
            val someType = Type("SomeType", "com.example")
            val provides =
                Provides(
                    isSingleton = false,
                    paramTypes = listOf(someType),
                    type = coffeeType,
                    body =
                        """
                        { Coffee(get<SomeType>()) }
                        """.trimIndent(),
                )
            val providesModule =
                ProvidesModule(
                    provides = listOf(provides),
                )

            val result = mapper.map(listOf(providesModule))

            Then("Params should be the type") {
                val typeSpec = result.first()
                val providesFunction = typeSpec.funSpecs.first()
                providesFunction.parameters.first().name shouldBe "someType"
                providesFunction.parameters.first().type.toString() shouldBe "com.example.SomeType"
            }

            Then("Return type should be correct") {
                val typeSpec = result.first()
                val providesFunction = typeSpec.funSpecs.first()
                providesFunction.returnType.toString() shouldBe "com.coffee.Coffee"
            }

            Then("Check print form.") {
                val typeSpec = result.first()
                val code = typeSpec.toString()
                code shouldContain "@dagger.Provides"
                code shouldContain "fun providesCoffee(someType: com.example.SomeType): com.coffee.Coffee"
                code shouldContain "{ Coffee(someType) }"
            }
        }

        When("Mapping a case with params") {
            val someType1 = Type("SomeType1", "com.example")
            val someType2 = Type("SomeType2", "com.example")
            val provides =
                Provides(
                    isSingleton = false,
                    paramTypes = listOf(someType1, someType2),
                    type = coffeeType,
                    body =
                        """
                        { Coffee(get<SomeType1>(), get<SomeType2>()) }
                        """.trimIndent(),
                )
            val providesModule =
                ProvidesModule(
                    provides = listOf(provides),
                )

            val result = mapper.map(listOf(providesModule))

            Then("Params should be all") {
                val typeSpec = result.first()
                val code = typeSpec.toString()
                code shouldContain "fun providesCoffee(someType1: com.example.SomeType1, someType2: com.example.SomeType2): com.coffee.Coffee"
                code shouldContain "{ Coffee(someType1, someType2) }"
            }
        }

        When("Mapping multiple modules") {
            val provides1 =
                Provides(
                    isSingleton = false,
                    paramTypes = emptyList(),
                    type = coffeeType1,
                    body =
                        """
                        { Coffee1() }
                        """.trimIndent(),
                )
            val providesModule1 =
                ProvidesModule(
                    provides = listOf(provides1),
                )
            val provides2 =
                Provides(
                    isSingleton = false,
                    paramTypes = emptyList(),
                    type = coffeeType2,
                    body =
                        """
                        { Coffee2() }
                        """.trimIndent(),
                )
            val providesModule2 =
                ProvidesModule(
                    provides = listOf(provides2),
                )

            val result = mapper.map(listOf(providesModule1, providesModule2))

            Then("Should generate multiple module classes") {
                result shouldHaveSize 2
                result[0].name shouldBe "ModuleCoffee1"
                result[1].name shouldBe "ModuleCoffee2"
            }
        }

        When("Mapping a module with multiple provides") {
            val provides1 =
                Provides(
                    isSingleton = false,
                    paramTypes = emptyList(),
                    type = coffeeType1,
                    body =
                        """
                        { Coffee1() }
                        """.trimIndent(),
                )
            val provides2 =
                Provides(
                    isSingleton = true,
                    paramTypes = emptyList(),
                    type = coffeeType2,
                    body =
                        """
                        { Coffee2() }
                        """.trimIndent(),
                )

            val providesModule =
                ProvidesModule(
                    provides = listOf(provides1, provides2),
                )

            val result = mapper.map(listOf(providesModule))

            Then("Should generate a module with multiple provides functions") {
                val typeSpec = result.first()
                typeSpec.funSpecs shouldHaveSize 2

                val providesFunction1 = typeSpec.funSpecs.first()
                providesFunction1.name shouldBe "providesCoffee1"
                providesFunction1.annotations.any { "Singleton" in it.typeName.toString() } shouldBe false

                val providesFunction2 = typeSpec.funSpecs[1]
                providesFunction2.name shouldBe "providesCoffee2"
                providesFunction2.annotations.any { "Singleton" in it.typeName.toString() } shouldBe true
            }
        }
    }
})
