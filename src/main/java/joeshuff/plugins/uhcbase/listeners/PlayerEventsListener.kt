package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerAdvancementDoneEvent

class PlayerEventsListener(val game: UHC): Listener, Stoppable {

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

    /**
     * This event is to stop spectators damaging contestants
     */
    @EventHandler
    fun playerDamagePlayerEvent(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            val damager = event.damager as Player

            if (game.isSpectator(damager)) {
                event.isCancelled = true
                return
            }
        }
    }

    /**
     * This event stops spectators from receiving damage during the game and any damage from being received in the hub
     */
    @EventHandler
    fun playerDamageEvent(event: EntityDamageEvent) {
        if (event.entity !is Player) return
        val player = event.entity as Player

        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(player)) {
            event.isCancelled = true
            return
        }

        if (event.entity.world.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

    /**
     * This event stops spectators from suffering hunger changes
     */
    @EventHandler
    fun foodChange(event: FoodLevelChangeEvent) {
        if (event.entity !is Player) return

        val player = event.entity as Player

        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(player)) {
            event.isCancelled = true
            return
        }

        if (event.entity.world.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

    /**
     * This event stops spectators from placing blocks in the game and
     * non-op players placing blocks in the hub
     */
    @EventHandler
    fun placeBlock(event: BlockPlaceEvent) {
        val player = event.player

        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(player)) {
            event.isCancelled = true
            return
        }

        if (player.world.name == Constants.hubWorldName && !player.isOp) {
            player.sendMessage("${ChatColor.RED}The hub world is protected")
            event.isCancelled = true
            return
        }

        if (game.state != UHC.GAME_STATE.IN_GAME && !player.isOp) {
            event.isCancelled = true
            return
        }
    }

    /**
     * This event stops spectators from breaking blocks in the game and
     * non-op players placing blocks in the hub
     */
    @EventHandler(priority = EventPriority.HIGH)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (game.state == UHC.GAME_STATE.IN_GAME && game.isSpectator(player)) {
            event.isCancelled = true
            return
        }

        if (player.world.name == Constants.hubWorldName && !player.isOp) {
            player.sendMessage("${ChatColor.RED}The hub world is protected")
            event.isCancelled = true
            return
        }

        if (game.state != UHC.GAME_STATE.IN_GAME && !player.isOp) {
            event.isCancelled = true
            return
        }
    }

    /**
     * This event prevents entites from targeting spectators
     */
    @EventHandler
    fun targetPlayer(event: EntityTargetLivingEntityEvent) {
        if (event.target == null) return

        if (event.target is Player) {
            val player = event.target as Player
            if (game.isSpectator(player)) {
                event.isCancelled = true
                return
            }
        }
    }
}