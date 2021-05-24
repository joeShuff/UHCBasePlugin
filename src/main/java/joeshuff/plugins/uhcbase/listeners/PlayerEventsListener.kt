package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.utils.updatePlayerFlight
import org.bukkit.Bukkit
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
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerCommandSendEvent

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
     * This event stops any damage from being received in the hub
     */
    @EventHandler
    fun playerDamageEvent(event: EntityDamageEvent) {
        if (event.isCancelled) return
        if (event.entity !is Player) return

        if (event.entity.world.name == Constants.hubWorldName) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun opEvent(event: PlayerCommandPreprocessEvent) {
        if (game.state != UHC.GAME_STATE.PRE_GAME) return

        if (event.message.startsWith("/op")) {
            val playername = event.message.replace("/op", "").trim()

            Bukkit.getOnlinePlayers().firstOrNull { it.name == playername }?.let {
                it.isOp = true
                game.updatePlayerFlight()
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.GREEN}Made $playername a server operator")
            }
        }

        if (event.message.startsWith("/deop")) {
            val playername = event.message.replace("/deop", "").trim()

            Bukkit.getOnlinePlayers().firstOrNull { it.name == playername }?.let {
                it.isOp = false
                game.updatePlayerFlight()
                event.isCancelled = true
                event.player.sendMessage("${ChatColor.RED}Made $playername no longer a server operator")
            }
        }
    }

    /**
     * This event stops players in the hub from suffering hunger changes
     */
    @EventHandler
    fun foodChange(event: FoodLevelChangeEvent) {
        if (event.isCancelled) return
        if (event.entity !is Player) return

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
        if (event.isCancelled) return
        val player = event.player

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
        if (event.isCancelled) return
        val player = event.player

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
}