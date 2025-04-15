package jardownloader

interface SystemPropertiesProvider {
    fun getKotlinVersion(): String

    fun getCoreVersion(): String
}

class SystemPropertiesProviderImpl : SystemPropertiesProvider {
    override fun getKotlinVersion(): String {
        return "1.9.25"
    }

    override fun getCoreVersion(): String {
        return "0.1.2"
    }
}
