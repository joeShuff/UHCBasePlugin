package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigDeathLightning(controller: ConfigController):
        ConfigItem<Boolean>(controller, "death-lightning", true, true) {

        override fun onSet(value: Boolean) {

        }
}