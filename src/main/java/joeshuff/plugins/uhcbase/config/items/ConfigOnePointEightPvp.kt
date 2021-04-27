package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem
import org.bukkit.attribute.Attribute

class ConfigOnePointEightPvp(val controller: ConfigController):
    ConfigItem<Boolean>(controller, "1-8-pvp", false, true) {

    override fun onSet(value: Boolean) {
        controller.game.getAllPlayers().forEach {
            it.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = if (value) 16.0 else 4.0
        }
    }
}