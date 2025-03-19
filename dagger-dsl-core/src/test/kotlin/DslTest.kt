import dagger.dsl.core.component
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class DslTest : StringSpec({

    "should create singleton component" {
        val component =
            component {
                singleton()
            }

        component.isSingleton shouldBe true
    }

    "should add module and bind classes" {
        val component =
            component {
                module {
                    binds<Service>(ServiceImpl::class.java)
                }
            }

        component.modules.shouldHaveSize(1)
        val module = component.modules.first()
        module.name shouldBe null
        module.binds.shouldContain(Service::class.java to ServiceImpl::class.java)
    }
})

interface Service

class ServiceImpl : Service
