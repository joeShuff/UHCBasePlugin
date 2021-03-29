package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TeamNameCommand(val plugin: UHCBase): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command is only for players.")
            return true
        }

        plugin.liveGameListener?.let {
            if (it.deadList.contains(sender.name)) {
                sender.sendMessage("${ChatColor.RED}You can't change the team name when you are dead")
                return true
            }
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        val newTeamName = args.joinToString(" ")

        if (newTeamName.length > 16) {
            sender.sendMessage("${ChatColor.RED}Team names cannot exceed 16 characters")
            return true
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val theirTeam = scoreboard.getEntryTeam(sender.name)

        theirTeam?.let {team ->
            team.displayName = newTeamName

            team.entries.forEach {teamEntry ->
                Bukkit.getPlayer(teamEntry)?.let { teamPlayer ->
                    teamPlayer.sendMessage("${ChatColor.GREEN}Team name changed to ${team.color}${newTeamName}${ChatColor.GREEN} by ${team.color}${sender.displayName}")
                }
            }
        }?: run {
            sender.sendMessage("${ChatColor.RED}You are not on a team.")
        }

        return true
    }

}