package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigTeleportDelay(controller: ConfigController): ConfigItem<Int>(controller, "teleport-delay", 4, minInt = 1) {
    override fun onSet(value: Int) {

    }
}