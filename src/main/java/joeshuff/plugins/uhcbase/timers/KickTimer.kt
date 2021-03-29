package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.listeners.GameListener
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class KickTimer(val listener: GameListener): BukkitRunnable() {

    companion object {
        /** Seconds til logged out players are killed **/
        const val LOGOUT_TIME = 60
    }

    class PlayerLogOut(
            val player: Player,
            val logOutTime: Int
    )

    class PlayerKick(
            val player: Player,
            val kickAt: Int
    )

    val plugin = listener.plugin

    var loggedOutList: ArrayList<PlayerLogOut> = arrayListOf()

    var kickUsersAt: ArrayList<PlayerKick> = arrayListOf()

    init {
        runTaskTimer(plugin, 0, 20)
    }

    fun stop() {
        cancel()
    }

    override fun run() {
        if (!plugin.UHCLive) {
            cancel()
            return
        }

        val nowSeconds = (System.currentTimeMillis() / 1000).toInt()

        kickUsersAt.forEach {
            if (it.player.isOnline && it.kickAt <= nowSeconds) {
                var kickMessage = "Thanks for playing!"
                val deathMessage = listener.kickMessages[it.player.name]?: ""
                val playername = it.player.displayName

                plugin.getConfigController().loadConfigFile("customize")?.let {
                    it.getString("kick_message")
                            ?.replace("{kickmessage}", deathMessage)
                            ?.replace("{playername}", playername)
                            ?.replace("\\n", "\n")
                            ?.let {
                        kickMessage = it
                    }
                }

                it.player.isWhitelisted = false
                it.player.kickPlayer(kickMessage)
            }
        }

        val iterateLoggedOut = arrayListOf<PlayerLogOut>()
        iterateLoggedOut.addAll(loggedOutList.filter { it.player.name !in listener.deadList })

        iterateLoggedOut.forEach {
            val player = it.player

            if (nowSeconds - it.logOutTime >= LOGOUT_TIME) {

                player.inventory.contents.filterNotNull().forEach {
                    player.location.world?.dropItemNaturally(player.location, it)
                }

                player.inventory.clear()

                val message = "${it.player.displayName} has been killed for inactivity"
                listener.handlePlayerDeath(it.player, message, "You were killed for being logged off for too long.", true)
            }
        }
    }
}