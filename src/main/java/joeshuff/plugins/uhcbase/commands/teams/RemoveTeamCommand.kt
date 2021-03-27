package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class RemoveTeamCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val teamByName = scoreboard.getTeam(args[0])
        val teamByPlayer = scoreboard.getEntryTeam(args[0])

        listOfNotNull(teamByName, teamByPlayer).firstOrNull()?.let {
            val teamname = it.color.toString() + it.displayName
            it.unregister()
            sender.sendMessage("${ChatColor.GREEN}Successfully removed team $teamname")
        }?: run {
            sender.sendMessage("${ChatColor.RED}Cannot find team or player team by name ${args[0]}")
        }

        return true
    }

}