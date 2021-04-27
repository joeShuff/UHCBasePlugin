package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigNetherEnabled(controller: ConfigController):
    ConfigItem<Boolean>(controller, "nether-enabled", true) {

    override fun onSet(value: Boolean) {

    }
}