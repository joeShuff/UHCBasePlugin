package joeshuff.plugins.uhcbase.config

class ConfigItem<T>(private val controller: ConfigController,
                    val configKey: String,
                    private val default: T,
                    private val minInt: Int? = null,
                    private val maxInt: Int? = null,
                    private val minDouble: Double? = null,
                    private val maxDouble: Double? = null,
                    private val onSet: ((T) -> Unit)? = null) {

    fun get(): T {
        return controller.getFromConfig(configKey) as T?: default
    }

    fun getDefault(): T = default

    fun set(value: Any) {
        controller.setToConfig(configKey, value)
        onSet?.invoke(get())
    }

    fun getLimits(): String {
        when (default) {
            is Int -> {
                return if (minInt != null && maxInt != null) {
                    "between $minInt and $maxInt (inclusive)"
                } else if (minInt != null) {
                    "at least $minInt"
                } else {
                    "at most $maxInt"
                }
            }
            is Double -> {
                return if (minDouble != null && maxDouble != null) {
                    "between $minDouble and $maxDouble (inclusive)"
                } else if (minDouble != null) {
                    "at least $minDouble"
                } else {
                    "at most $maxDouble"
                }
            }
            else -> return ""
        }
    }

    fun isValid(newValue: Any?): Boolean {
        if (newValue == null) return false

        return when (newValue) {
            is Int -> {
                (minInt?: Integer.MIN_VALUE) <= newValue && newValue <= (maxInt?: Integer.MAX_VALUE)
            }
            is Double -> {
                (minDouble?: Double.MIN_VALUE) <= newValue && newValue <= (maxDouble?: Double.MAX_VALUE)
            }
            else -> {
                true
            }
        }
    }
}