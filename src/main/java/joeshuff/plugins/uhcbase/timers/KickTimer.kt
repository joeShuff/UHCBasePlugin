package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class KickTimer(val game: UHC): BukkitRunnable() {

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

    val plugin = game.plugin

    var loggedOutList: ArrayList<PlayerLogOut> = arrayListOf()

    var kickUsersAt: ArrayList<PlayerKick> = arrayListOf()

    init {
        runTaskTimer(plugin, 0, 20)

        game.gameState
            .distinctUntilChanged()
            .subscribe {
                if (it == UHC.GAME_STATE.IN_GAME) {
                    loggedOutList.clear()
                    kickUsersAt.clear()
                }
            }
    }

    fun stop() {
        cancel()
    }

    override fun run() {
        if (game.state != UHC.GAME_STATE.IN_GAME) return

        val nowSeconds = (System.currentTimeMillis() / 1000).toInt()

        kickUsersAt.forEach {
            if (it.player.isOnline && it.kickAt <= nowSeconds) {
                var kickMessage = "Thanks for playing!"
                val deathMessage = game.kickMessages[it.player.name]?: ""
                val playername = it.player.displayName

                game.configController.loadConfigFile("customize")?.let {
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
        iterateLoggedOut.addAll(loggedOutList.filter { !game.isPlayerDead(it.player) })

        iterateLoggedOut.forEach {
            val player = it.player

            if (nowSeconds - it.logOutTime >= LOGOUT_TIME) {

                player.inventory.contents.filterNotNull().forEach {
                    player.location.world?.dropItemNaturally(player.location, it)
                }

                player.inventory.clear()

                val message = "${it.player.displayName} has been killed for inactivity"
                game.liveGameListener.handlePlayerDeath(it.player, message, "You were killed for being logged off for too long.", true)
            }
        }
    }
}