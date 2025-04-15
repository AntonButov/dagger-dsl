import javax.inject.Inject

/** An electric heater to heat the coffee.  */
class ElectricHeater
    @Inject
    internal constructor(private val logger: CoffeeLogger) : Heater {
        private var heating = false

        override fun on() {
            this.heating = true
            logger.log("~ ~ ~ heating ~ ~ ~")
        }

        override fun off() {
            this.heating = false
        }

        override val isHot: Boolean
            get() = this.heating
    }
