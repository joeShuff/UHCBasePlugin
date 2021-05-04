package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigBorderSize(controller: ConfigController):
    ConfigItem<Int>(controller, "border-size", 1000, true, minInt = 1) {

    override fun onSet(value: Int) {

    }

}