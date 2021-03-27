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

    fun onPlayerDeath(deadPlayer: Player) {
        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard ?: return

        if (respectTeams) {
            val deadPlayersTeam = scoreboard.getEntryTeam(deadPlayer.name)

            deadPlayersTeam?.let {eliminatedTeam ->
                if (eliminatedTeam.allPlayersDead() && aliveTeams.contains(eliminatedTeam.name)) {
                    aliveTeams.remove(eliminatedTeam.name)
                    plugin.server.broadcastMessage("${ChatColor.RED}Team ${eliminatedTeam.prefix}${eliminatedTeam.displayName}${ChatColor.RED} HAS BEEN ELIMINATED")

                    when (aliveTeams.size) {
                        2 -> {
                            thirdPlace = eliminatedTeam.prefix + eliminatedTeam.displayName
                            plugin.server.broadcastMessage("$thirdPlace have finished third!")
                        }
                        1 -> {
                            secondPlace = eliminatedTeam.prefix + eliminatedTeam.displayName
                            plugin.server.broadcastMessage("$thirdPlace have finished second!")

                            val winningTeam = scoreboard.getTeam(aliveTeams[0])
                            winningTeam?.let {
                                firstPlace = winningTeam.prefix + winningTeam.displayName
                                plugin.server.broadcastMessage("$firstPlace have won!!!")

                                VictoryTimer(plugin, true, firstPlace)
                            }
                        }
                    }

                }
            }
        } else {
            if (aliveTeams.contains(deadPlayer.name)) {
                aliveTeams.remove(deadPlayer.name)

                val prefix = scoreboard.getEntryTeam(deadPlayer.name)?.prefix?: ""

                plugin.server.broadcastMessage("$prefix${deadPlayer.displayName} ${ChatColor.RED}has been eliminated!")

                when (aliveTeams.size) {
                    2 -> {
                        thirdPlace = prefix + deadPlayer.displayName
                        plugin.server.broadcastMessage("$thirdPlace has finished third!")
                    }
                    1 -> {
                        secondPlace = prefix + deadPlayer.displayName
                        plugin.server.broadcastMessage("$thirdPlace has finished second!")

                        plugin.server.getPlayer(aliveTeams[0])?.let {winner ->
                            val winningTeamPrefix = scoreboard.getEntryTeam(winner.name)?.prefix?: ""

                            firstPlace = winningTeamPrefix + winner.displayName
                            plugin.server.broadcastMessage("$firstPlace has won!!!")

                            VictoryTimer(plugin, true, firstPlace)
                        }
                    }
                }
            }
        }
    }

    private fun Team.allPlayersDead(): Boolean {
        for (entry in entries) {
            val entryPlayer = plugin.server.offlinePlayers.firstOrNull { it.name == entry }

            entryPlayer?.let { teamPlayer ->
                plugin.livePlayerListener?.let { listener ->
                    if (!listener.deadList.contains(teamPlayer.name) && listener.playingList.contains(teamPlayer.name)) {
                        return false
                    }
                }
            }
        }

        return true
    }
}