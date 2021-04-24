package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class EntityListener(val game: UHC): Listener, Stoppable {

    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onAchieve(event: PlayerAdvancementDoneEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) {
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
            event.drops.removeIf { it.type == Material.ENDER_PEARL }

            if (Random.nextInt(0, 100) <= plugin.getConfigController().PEARL_RATE.get()) {
                event.drops.add(ItemStack(Material.ENDER_PEARL, 1))
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