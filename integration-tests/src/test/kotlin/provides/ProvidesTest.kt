package provides

import DaggerComponentDsl
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull

class ProvidesTest : StringSpec({
    "Component should return VeryImportantClass" {
        val component = DaggerComponentDsl.create()
        val veryImportantClass = component.getVeryImportedImplementation()
        veryImportantClass shouldNotBeNull {}
    }

    "Component should should contain a param." {
        val component = DaggerComponentDsl.create()
        val veryImportantClass = component.getVeryImportedImplementation()
        veryImportantClass.param shouldNotBeNull {}
    }
})
