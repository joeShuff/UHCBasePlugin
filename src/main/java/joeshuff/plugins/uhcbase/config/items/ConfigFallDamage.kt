package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigFallDamage(controller: ConfigController):
        ConfigItem<Boolean>(controller, "fall-damage", true, true) {

        override fun onSet(value: Boolean) {

        }
}