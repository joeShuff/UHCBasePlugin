package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.utils.TeamsUtils
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TeamChatCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command is for players only")
            return true
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        scoreboard.getEntryTeam(sender.name)?.let { team ->
            if (args.isEmpty()) return command.notifyCorrectUsage(sender)

            val message = args.joinToString(" ")

            team.entries.forEach {entryName ->
                plugin.server.getPlayer(entryName)?.let {teammate ->
                    teammate.sendMessage("${ChatColor.GREEN}[TEAM] ${sender.displayName}${ChatColor.GRAY} > ${ChatColor.WHITE}$message")
                }
            }
        }?: run {
            sender.sendMessage("${ChatColor.RED}You are not part of a team!")
        }

        return true
    }

}