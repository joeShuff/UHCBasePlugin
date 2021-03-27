package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.utils.TeamsUtils
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class ClearTeamsCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permission to use this command!")
                return true
            }
        }

        TeamsUtils.removeAllTeams(plugin)

        sender.sendMessage("${ChatColor.GREEN}All teams have been removed.")

        return true
    }
}