package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigFriendlyFire(controller: ConfigController):
    ConfigItem<Boolean>(controller, "friendly-fire", true, true) {

    override fun onSet(value: Boolean) {

    }
}