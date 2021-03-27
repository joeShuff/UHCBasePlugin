package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.World
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerPortalEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.plugin.java.JavaPlugin

class PortalHandler(val plugin: JavaPlugin): Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    @EventHandler
    fun onTeleport(event: PlayerPortalEvent) {
        event.searchRadius = plugin.getConfigController().NETHER_PORTAL_RADIUS.get()
        event.creationRadius = plugin.getConfigController().NETHER_PORTAL_RADIUS.get()

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