package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigPregenTicks(controller: ConfigController):
    ConfigItem<Int>(controller, "pregen-ticks", 20, minInt = 1) {
    override fun onSet(value: Int) {

    }
}