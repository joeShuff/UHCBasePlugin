package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.utils.WorldUtils
import joeshuff.plugins.uhcbase.utils.removeAllAdvancements
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class EntityListener(val plugin: UHCBase): Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = if (plugin.getConfigController().ONE_POINT_EIGHT_PVP.get()) 16.0 else 4.0

        plugin.getConfigController().loadConfigFile("customize")?.let {
            var title = it.getString("join_title")?: ""
            var subtitle = it.getString("join_subtitle")?: ""

            title = title.replace("{playername}", player.displayName)
            subtitle = subtitle.replace("{playername}", player.displayName)

            player.sendTitle(title, subtitle, 10, 70, 20)
        }

        player.sendDefaultTabInfo(plugin)

        if (!plugin.UHCLive && !plugin.UHCPrepped && Bukkit.getWorld(Constants.hubWorldName) != null) {
            player.teleport(WorldUtils.getHubSpawnLocation())
            player.gameMode = GameMode.ADVENTURE
        }
    }

    @EventHandler
    fun onAchieve(event: PlayerAdvancementDoneEvent) {
        if (!plugin.UHCLive) {
            //We are no longer able to stop an advancement from being achieved outside of the UHC
            //However, all advancements are cleared when the UHC starts so it should be fine to ignore this
        }
    }

    @EventHandler
    fun entitySpawn(event: EntitySpawnEvent) {
        if (event.location.world?.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun entityDeath(event: EntityDeathEvent) {
        if (event.entityType == EntityType.ENDERMAN) {
            if (Random.nextInt(0, 100) <= plugin.getConfigController().PEARL_RATE.get()) {
                event.entity.world.dropItemNaturally(event.entity.location, ItemStack(Material.ENDER_PEARL, 1))
            }
        }
    }

    @EventHandler
    fun entityDamage(event: EntityDamageEvent) {
        if (event.entity !is Player) return

        if (event.entity.world.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun foodChange(event: FoodLevelChangeEvent) {
        if (event.entity !is Player) return

        if (event.entity.world.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

}