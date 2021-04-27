package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigTeleportSize(controller: ConfigController): ConfigItem<Int>(controller, "teleport-size", 5, minInt = 1) {
    override fun onSet(value: Int) {

    }
}