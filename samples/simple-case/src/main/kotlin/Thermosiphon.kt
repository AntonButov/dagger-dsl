import javax.inject.Inject

/** A thermosiphon to pump the coffee.  */
class Thermosiphon
    @Inject
    internal constructor(private val logger: CoffeeLogger, private val heater: Heater) : Pump {
        override fun pump() {
            if (heater.isHot) {
                logger.log("=> => pumping => =>")
            }
        }
    }
