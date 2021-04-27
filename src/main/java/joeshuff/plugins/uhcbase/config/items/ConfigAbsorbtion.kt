package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem
import org.bukkit.Bukkit
import org.bukkit.potion.PotionEffectType

class ConfigAbsorbtion(controller: ConfigController):
        ConfigItem<Boolean>(controller, "absorbtion", true, true) {

    override fun onSet(value: Boolean) {
        if (!value) Bukkit.getOnlinePlayers().forEach { it.removePotionEffect(PotionEffectType.ABSORPTION) }
    }
}