package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.block.Container
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent

class SpectatorEvents(val game: UHC): Listener, Stoppable {
    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    /**
     * This event stops spectators from lowering their food level
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun foodChange(event: FoodLevelChangeEvent) {
        if (event.entity is Player) {
            if (game.isSpectator(event.entity as Player)) {
                event.isCancelled = true
            }
        }
    }

    /**
     * This stops damage to or from spectators
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun damage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player) {
            if (game.isSpectator(event.entity as Player)) {
                event.isCancelled = true
            }
        }

        if (event.damager is Player) {
            if (game.isSpectator(event.damager as Player)) {
                event.isCancelled = true
            }
        }
    }

    /**
     * Stops spectators from placing blocks in game
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun blockPlace(event: BlockPlaceEvent) {
        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(event.player)) {
            event.isCancelled = true
            return
        }
    }

    /**
     * Stops spectators from breaking blocks in game
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun blockBreak(event: BlockBreakEvent) {
        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(event.player)) {
            event.isCancelled = true
            return
        }
    }

    /**
     * This stops other entities from targeting a spectator
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun targetPlayer(event: EntityTargetLivingEntityEvent) {
        if (event.target == null) return

        if (event.target is Player) {
            if (game.isSpectator(event.target as Player)) {
                event.isCancelled = true
                return
            }
        }
    }

    /**
     * This stops players from taking things out of inventories in game
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun invClick(event: InventoryClickEvent) {
        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(event.whoClicked as Player)) {
            event.isCancelled = true

            //TODO: INV CONTROLS

            return
        }
    }

    /**
     * This stops spectators from clicking on blocks to enter beds etc.
     * If its an inventory, open it but no open animation.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    fun blockClick(event: PlayerInteractEvent) {
        if (game.isSpectator(event.player)) {
            event.isCancelled = true

            event.clickedBlock?.state?.let {
                if (it is Container) {
                    event.player.openInventory(it.inventory)
                }

                plugin.logger.info("hi")
            }
        }
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

}