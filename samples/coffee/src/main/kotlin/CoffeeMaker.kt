import dagger.Lazy
import javax.inject.Inject

/** A coffee maker to brew the coffee.  */
class CoffeeMaker
    @Inject
    internal constructor(
        private val logger: CoffeeLogger, // Create a possibly costly heater only when we use it.
        private val heater: Lazy<Heater>,
        private val pump: Pump,
    ) {
        fun brew() {
            heater.get().on()
            pump.pump()
            logger.log(" [_]P coffee! [_]P ")
            heater.get().off()
        }
    }
