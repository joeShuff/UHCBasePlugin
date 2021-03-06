package joeshuff.plugins.uhcbase

import joeshuff.plugins.uhcbase.timers.VictoryTimer
import joeshuff.plugins.uhcbase.utils.TeamsUtils
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team


class PositionsController(val plugin: UHCBase, private val respectTeams: Boolean) {
    private val aliveTeams = arrayListOf<String>()

    var firstPlace: String = ""
    var secondPlace: String = ""
    var thirdPlace: String = ""

    fun stillAlive(): String {
        return "${aliveTeams.size} ${if (respectTeams) "Teams" else "Players"}"
    }

    init {
        if (respectTeams) {
            aliveTeams.addAll(TeamsUtils.getOnlineTeams().map { it.name })
        } else {
            aliveTeams.addAll(plugin.server.onlinePlayers.map { it.name })
        }
    }

    fun elimination(eliminated: String, prefix: String, displayName: String) {
        aliveTeams.remove(eliminated)

        val haveHas = if (respectTeams) "have" else "has"

        if (respectTeams) {
            plugin.server.broadcastMessage("${ChatColor.RED}Team ${prefix}${displayName}${ChatColor.RED} HAVE BEEN ELIMINATED")
        }

        when (aliveTeams.size) {
            2 -> {
                thirdPlace = prefix + displayName
                plugin.server.broadcastMessage("$prefix$thirdPlace ${ChatColor.DARK_GRAY}$haveHas finished third!")
            }
            1 -> {
                secondPlace = prefix + displayName
                plugin.server.broadcastMessage("$prefix$secondPlace ${ChatColor.GRAY}$haveHas finished second!")

                if (respectTeams) {
                    val winningTeam = plugin.server.scoreboardManager?.mainScoreboard?.getTeam(aliveTeams[0])
                    winningTeam?.let {
                        firstPlace = winningTeam.prefix + winningTeam.displayName
                        plugin.server.broadcastMessage("$prefix$firstPlace ${ChatColor.GOLD} $haveHas won!!!")

                        VictoryTimer(plugin, true, firstPlace)
                    }
                } else {
                    plugin.server.getPlayer(aliveTeams[0])?.let {winner ->
                        val winningTeamPrefix = plugin.server.scoreboardManager?.mainScoreboard?.getEntryTeam(winner.name)?.prefix?: ""

                        firstPlace = winningTeamPrefix + winner.displayName
                        plugin.server.broadcastMessage("$prefix$firstPlace ${ChatColor.GOLD} $haveHas won!!!")

                        VictoryTimer(plugin, false, firstPlace)
                    }
                }
            }
        }
    }

    fun onPlayerDeath(deadPlayer: Player) {
        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard ?: return

        if (respectTeams) {
            val deadPlayersTeam = scoreboard.getEntryTeam(deadPlayer.name)

            deadPlayersTeam?.let {eliminatedTeam ->
                if (eliminatedTeam.allPlayersDead() && aliveTeams.contains(eliminatedTeam.name)) {
                    elimination(eliminatedTeam.name, eliminatedTeam.prefix, eliminatedTeam.displayName)
                }
            }
        } else {
            if (aliveTeams.contains(deadPlayer.name)) {
                val prefix = scoreboard.getEntryTeam(deadPlayer.name)?.prefix?: ""
                elimination(deadPlayer.name, prefix, deadPlayer.displayName)
            }
        }
    }

    private fun Team.allPlayersDead(): Boolean {
        for (entry in entries) {
            val entryPlayer = plugin.server.offlinePlayers.firstOrNull { it.name == entry }

            entryPlayer?.let { teamPlayer ->
                plugin.liveGameListener?.let { listener ->
                    if (!listener.deadList.contains(teamPlayer.name) && listener.playingList.contains(teamPlayer.name)) {
                        return false
                    }
                }
            }
        }

        return true
    }
}