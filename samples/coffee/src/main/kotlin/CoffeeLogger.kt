import javax.inject.Inject
import javax.inject.Singleton

/** A logger to logs steps while brewing coffee.  */
@Singleton
class CoffeeLogger
    @Inject
    internal constructor() {
        private val logs: MutableList<String> = mutableListOf()

        fun log(msg: String) {
            logs.add(msg)
        }

        fun logs(): MutableList<String> {
            return logs
        }
    }
