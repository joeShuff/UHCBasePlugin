package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Scoreboard
import org.bukkit.scoreboard.Team
import kotlin.math.ceil
import kotlin.math.roundToInt

class TeamsUtils {
    companion object {
        /**
         * This method returns a list of teams that have at least one player online
         */
        fun getOnlineTeams(): List<Team> {
            val teams = arrayListOf<Team>()

            for (team in Bukkit.getServer().scoreboardManager?.mainScoreboard?.teams?: emptyList()) {
                var playersOnline = false

                for (player in team.entries) {
                    if (Bukkit.getServer().getPlayer(player) != null) {
                        playersOnline = true
                    }
                }

                if (playersOnline) {
                    teams.add(team)
                }
            }

            return teams
        }

        /**
         * Function to randomly create teams of size x
         *
         * returns true if valid input, false if not
         */
        fun createTeams(plugin: JavaPlugin, sender: CommandSender, command: Command, args: Array<out String>): Boolean {
            if (args.size != 1) {
                return command.notifyCorrectUsage(sender)
            } else {

                val board = Bukkit.getServer().scoreboardManager?.mainScoreboard

                if (board == null) {
                    sender.sendMessage("${ChatColor.RED}Something went wrong retrieving the main scoreboard. Please try again.")
                    return true
                }

                if (board.teams.size != 0) {
                    sender.sendMessage("${ChatColor.RED}Unable to add teams as some already exist. Do /clearteams then retry.")
                    return true
                }

                var playersPerTeam = args[0].toIntOrNull()?: -1

                if (playersPerTeam <= 0) {
                    sender.sendMessage("${ChatColor.RED}Invalid players per team. Try any integer over 0.")
                    return true
                }

                val players = arrayListOf<Player>()

                Bukkit.getServer().onlinePlayers.forEach {
                    if (it.gameMode != GameMode.SPECTATOR) {
                        players.add(it)
                    }
                }

                if (players.size < playersPerTeam) {
                    playersPerTeam = players.size
                    Bukkit.getServer().broadcastMessage("${ChatColor.RED}Players per team was greater than online players - reduced team size to $playersPerTeam")
                }

                Bukkit.getServer().broadcastMessage("Randomizing players into teams of $playersPerTeam players per team")

                var amountOfTeams = ceil((players.size.toDouble() / playersPerTeam.toDouble())).roundToInt()

                if (amountOfTeams > 48) {
                    sender.sendMessage("${ChatColor.RED}There is a maximum of 48 teams. Your input generated $amountOfTeams teams. Try increasing the team size.")
                    return true
                }

                val alreadyChosenStyles = arrayListOf<String>()

                for (teamId in 0..amountOfTeams) {
                    val newTeam = board.registerNewTeam("team_$teamId")

                    var color = getTeamColor()
                    var colorAttempts = 1

                    while (color.toString() in alreadyChosenStyles) {
                        color = getTeamColor()
                        colorAttempts ++
                    }

                    alreadyChosenStyles.add(color.toString())
                    plugin.logger.info("Color chosen after $colorAttempts attempts")

                    newTeam.color = color
                    newTeam.displayName = "Team $teamId"
                    newTeam.setCanSeeFriendlyInvisibles(true)
                    newTeam.setAllowFriendlyFire(plugin.getConfigController().FRIENDLY_FIRE.get())

                    val playersWithoutATeam = players.filter { board.getEntryTeam(it.name) == null }

                    if (playersWithoutATeam.isNotEmpty()) {
                        val theChosenOne = playersWithoutATeam.random()
                        newTeam.addEntry(theChosenOne.displayName)

                        theChosenOne.sendMessage("${ChatColor.BOLD.toString() + ChatColor.GOLD.toString()}You have been added to ${color}Team $teamId")
                    }
                }

                Bukkit.getServer().broadcastMessage(ChatColor.GREEN.toString() + "Successfully randomized $amountOfTeams players onto $amountOfTeams teams")

                return true
            }
        }

        /**
         * Function to create a team for a player name, it adds this playername to the team
         * and returns the newly created team
         */
        fun createTeamFor(scoreboard: Scoreboard, player: String): Team {
            scoreboard.getTeam("${player}-team")?.unregister()

            val newTeam = scoreboard.registerNewTeam("${player}-team")
            newTeam.displayName = "${player}'s Team"
            newTeam.addEntry(player)

            return newTeam
        }

        /**
         * This method regenerates colors for all teams
         */
        fun recolorAllTeams(plugin: JavaPlugin) {
            val alreadyChosenStyles = arrayListOf<String>()

            plugin.server.scoreboardManager?.mainScoreboard?.teams?.forEach {
                var color = getTeamColor()
                var colorAttempts = 1

                while (color.toString() in alreadyChosenStyles) {
                    color = getTeamColor()
                    colorAttempts ++
                }

                alreadyChosenStyles.add(color.toString())

                it.color = color
            }
        }

        /**
         * This method removes all teams
         */
        fun removeAllTeams(plugin: JavaPlugin) {
            plugin.server.scoreboardManager?.mainScoreboard?.teams?.forEach { it.unregister() }
        }

        /**
         * This method randomly chooses a combination of modifier + color text modifiers
         */
        fun getTeamColor(): ChatColor {
            val colors = ChatColor.values().filter { it.isColor }
            val effects = arrayListOf("", ChatColor.BOLD, ChatColor.UNDERLINE, ChatColor.ITALIC)

//            return effects.random().toString() + colors.random().toString()
            return colors.random()
        }
    }
}