package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.utils.showRules
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class RulesCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command is for players only.")
            return true
        }

        sender.showRules(plugin)

        return true
    }

}