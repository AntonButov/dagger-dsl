import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class CoffeeTest : FunSpec({

    test("Coffee component should be created") {
        DaggerCoffeeShopDsl.create() as CoffeeShopDsl
    }

    test("component should have logger") {
        DaggerCoffeeShopDsl.create().logger() shouldNotBeNull { log("I'm a logger") }
    }

    test("logger should be singleton") {
        val component = DaggerCoffeeShopDsl.create()
        val firstLogger = component.logger()
        val secondLogger = component.logger()
        firstLogger shouldBe secondLogger
    }

    test("component should have maker") {
        DaggerCoffeeShopDsl.create().maker() shouldNotBeNull { brew() }
    }
})
