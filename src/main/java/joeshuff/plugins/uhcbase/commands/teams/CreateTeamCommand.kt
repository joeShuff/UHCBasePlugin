package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.utils.TeamsUtils
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.Team

class CreateTeamCommand(val plugin: JavaPlugin): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permission to use this command!")
                return true
            }
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val firstPlayer = args[0]

        val newTeam = TeamsUtils.createTeamFor(scoreboard, firstPlayer)

        args.forEach {playerToAdd ->
            newTeam.addEntry(playerToAdd)

            plugin.server.getPlayer(playerToAdd)?.let {
                it.sendMessage("${ChatColor.GREEN}You have joined ${newTeam.displayName}")
            }
        }

        sender.sendMessage("${ChatColor.GREEN}Team ${newTeam.displayName} created with ${newTeam.entries.size} players")

        return true
    }
}