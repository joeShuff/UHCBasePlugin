package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigCanSpectate(controller: ConfigController):
    ConfigItem<Boolean>(controller, "can-spectate", true) {

    override fun onSet(value: Boolean) {

    }
}