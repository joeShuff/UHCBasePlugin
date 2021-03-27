package joeshuff.plugins.uhcbase.config

import java.lang.Exception

class InvalidParameterException(override val message: String): Exception(message) {
}