package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigGraceEndEpisode(controller: ConfigController):
    ConfigItem<Int>(controller, "grace-end-episode", 2, minInt = 0) {
    override fun onSet(value: Int) {

    }
}