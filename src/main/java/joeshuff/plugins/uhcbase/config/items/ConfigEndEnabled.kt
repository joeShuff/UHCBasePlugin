package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigEndEnabled(controller: ConfigController):
    ConfigItem<Boolean>(controller, "end-enabled", false, true) {
    override fun onSet(value: Boolean) {

    }
}