package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.utils.getHubSpawnLocation
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import joeshuff.plugins.uhcbase.utils.updatePlayerFlight
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinLeaveListener(val game: UHC): Listener, Stoppable {

    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player

        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)?.baseValue = if (game.configController.ONE_POINT_EIGHT_PVP.get()) 16.0 else 4.0

        game.updatePlayerFlight()

        game.configController.loadConfigFile("customize")?.let {
            var title = it.getString("join_title")?: ""
            var subtitle = it.getString("join_subtitle")?: ""

            title = title.replace("{playername}", player.displayName)
            subtitle = subtitle.replace("{playername}", player.displayName)

            player.sendTitle(title, subtitle, 10, 70, 20)
        }

        player.sendDefaultTabInfo(game)

        if (game.state == UHC.GAME_STATE.PRE_GAME && Bukkit.getWorld(Constants.hubWorldName) != null) {
            player.teleport(getHubSpawnLocation())
            player.gameMode = GameMode.ADVENTURE
        }
    }
}