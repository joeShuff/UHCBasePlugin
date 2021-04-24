package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent

class PortalHandler(val game: UHC): Listener, Stoppable {

    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onTeleport(event: PlayerPortalEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) {
            event.isCancelled = true
            return
        }

        if (event.from.world?.environment == World.Environment.NORMAL) {
            if (event.cause == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
                if (!plugin.getConfigController().NETHER_ENABLED.get()) {
                    event.isCancelled = true
                }
            }

            if (event.cause == PlayerTeleportEvent.TeleportCause.END_PORTAL) {
                if (!plugin.getConfigController().END_ENABLED.get()) {
                    event.isCancelled = true
                }
            }
        }
    }
}