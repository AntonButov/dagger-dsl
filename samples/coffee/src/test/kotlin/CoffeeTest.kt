import io.kotest.core.spec.style.FunSpec

class CoffeeTest : FunSpec({
    test("Coffee component should be created") {
        DaggerCoffeeShop.builder().build() is CoffeeShop
    }
})
