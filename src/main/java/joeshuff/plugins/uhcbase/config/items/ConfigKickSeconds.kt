package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigKickSeconds(controller: ConfigController):
    ConfigItem<Int>(controller, "seconds-until-kick", 30, minInt = 0) {

    override fun onSet(value: Int) {

    }

}