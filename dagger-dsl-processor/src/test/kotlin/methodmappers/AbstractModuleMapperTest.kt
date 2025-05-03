package methodmappers

import com.google.devtools.ksp.processing.Resolver
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import models.Bind
import models.BindTypes
import models.Type
import processor.psiUtils.Method
import typeFinders.BindImplFinder
import typeFinders.BindTypeFinder

class AbstractModuleMapperTest : BehaviorSpec({
    Given("An AbstractModuleMapper with mocked dependencies") {
        val resolver = mockk<Resolver>()
        val bindType = Type("SomeBindClass", "packageName")
        val bindTypeFinder =
            mockk<BindTypeFinder> {
                every { findByName(any(), any()) } returns bindType
            }

        val implType = Type("SomeBindClassImpl", "packageName")
        val bindImplFinder =
            mockk<BindImplFinder> {
                every { findByName(any(), any()) } returns implType
            }

        val abstractModuleMapper: AbstractModuleMapper =
            AbstractModuleMapperImpl(
                bindTypeFinder = bindTypeFinder,
                bindImplFinder = bindImplFinder,
            )

        When("Mapping a non-singleton bind method") {
            val bind =
                Method(
                    name = "bind",
                    genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                )

            Then("Should create a non-singleton bind") {
                val module = abstractModuleMapper.mapAbstractModule(listOf(bind), resolver)
                module.binds shouldBe
                    listOf(
                        Bind(
                            isSingleton = false,
                            bindTypes =
                                BindTypes(
                                    type = bindType,
                                    impl = implType,
                                ),
                        ),
                    )
            }
        }

        When("Mapping a singleton bind method") {
            val bind =
                Method(
                    name = "bindSingleton",
                    genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                )

            Then("Should create a singleton bind") {
                val module = abstractModuleMapper.mapAbstractModule(listOf(bind), resolver)
                module.binds shouldBe
                    listOf(
                        Bind(
                            isSingleton = true,
                            bindTypes =
                                BindTypes(
                                    type = bindType,
                                    impl = implType,
                                ),
                        ),
                    )
            }
        }

        When("Mapping multiple bind methods") {
            val binds =
                listOf(
                    Method(
                        name = "bind",
                        genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                    ),
                    Method(
                        name = "bindSingleton",
                        genericTypes = listOf("SomeBindClass", "SomeBindClassImpl"),
                    ),
                )

            Then("Should create multiple binds with correct singleton flags") {
                val module = abstractModuleMapper.mapAbstractModule(binds, resolver)
                module.binds.size shouldBe 2
                module.binds[0].isSingleton shouldBe false
                module.binds[1].isSingleton shouldBe true
            }
        }
    }
})
