package typeFinders

import compile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import models.Type

class ClassAndInterfaceTypeFinderImplTest : BehaviorSpec({

    Given("ClassAndInterfaceTypeFinderImpl") {
        val typeFinder = ClassAndInterfaceTypeFinderImpl()

        When("finding a class by name") {
            val className = "TestClass"
            val code =
                """
                package com.example
                
                class TestClass {
                    fun someMethod(): String = "test"
                }
                
                interface TestInterface {
                    fun interfaceMethod()
                }
                
                @DaggerDsl
                fun anyNameFunction() {
                    component<TestComponent> {
                        // Some component configuration
                    }
                }
                """.trimIndent()

            Then("should return the correct Type for the class") {
                code compile { resolver ->
                    val type = typeFinder.findByName(resolver, className)

                    type shouldNotBe null
                    type.shouldBeInstanceOf<Type>()
                    type.name shouldBe "TestClass"
                    type.packageName shouldBe "com.example"
                }
            }
        }

        When("finding an interface by name") {
            val interfaceName = "TestInterface"
            val code =
                """
                package com.example
                
                class TestClass {
                    fun someMethod(): String = "test"
                }
                
                interface TestInterface {
                    fun interfaceMethod()
                }
                
                @DaggerDsl
                fun anyNameFunction() {
                    component<TestComponent> {
                        // Some component configuration
                    }
                }
                """.trimIndent()

            Then("should return the correct Type for the interface") {
                code compile { resolver ->
                    val type = typeFinder.findByName(resolver, interfaceName)

                    type shouldNotBe null
                    type.shouldBeInstanceOf<Type>()
                    type.name shouldBe "TestInterface"
                    type.packageName shouldBe "com.example"
                }
            }
        }

        When("finding a class in the default package") {
            val className = "DefaultPackageClass"
            val code =
                """
                class DefaultPackageClass {
                    fun someMethod(): String = "test"
                }
                
                @DaggerDsl
                fun anyNameFunction() {
                    component<TestComponent> {
                        // Some component configuration
                    }
                }
                """.trimIndent()

            Then("should return the correct Type with empty package name") {
                code.compile { resolver ->
                    val type = typeFinder.findByName(resolver, className)

                    type shouldNotBe null
                    type.shouldBeInstanceOf<Type>()
                    type.name shouldBe "DefaultPackageClass"
                    type.packageName shouldBe ""
                }
            }
        }

        When("finding a class that doesn't exist") {
            val nonExistentClassName = "NonExistentClass"
            val code =
                """
                package com.example
                
                class TestClass {
                    fun someMethod(): String = "test"
                }
                
                @DaggerDsl
                fun anyNameFunction() {
                    component<TestComponent> {
                        // Some component configuration
                    }
                }
                """.trimIndent()

            Then("should throw an appropriate exception") {
                code compile { resolver ->
                    val exception =
                        shouldThrow<Exception> {
                            typeFinder.findByName(resolver, nonExistentClassName)
                        }
                    exception shouldNotBe null
                }
            }
        }
    }
})
