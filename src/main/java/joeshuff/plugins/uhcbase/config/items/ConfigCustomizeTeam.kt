package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigCustomizeTeam(controller: ConfigController):
    ConfigItem<Boolean>(controller, "customize-teams", true, announceChange = true) {

    override fun onSet(value: Boolean) {

    }
}