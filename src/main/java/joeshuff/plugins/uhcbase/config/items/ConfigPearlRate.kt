package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigPearlRate(controller: ConfigController):
    ConfigItem<Double>(controller, "pearl-rate", 0.5, true, minDouble = 0.0, maxDouble = 100.0) {

    override fun onSet(value: Double) {

    }
}