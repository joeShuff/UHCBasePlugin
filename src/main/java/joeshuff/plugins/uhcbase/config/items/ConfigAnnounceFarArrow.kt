package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigAnnounceFarArrow(controller: ConfigController): ConfigItem<Boolean>(controller, "announce-far-arrow", false, true) {
    override fun onSet(value: Boolean) {

    }
}