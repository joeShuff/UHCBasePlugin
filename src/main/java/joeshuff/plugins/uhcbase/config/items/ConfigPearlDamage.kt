package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigPearlDamage(controller: ConfigController):
        ConfigItem<Boolean>(controller, "pearl-damage", true, true) {

        override fun onSet(value: Boolean) {
        }
}