package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class HelpOpCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command is for players only")
            return true
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        val message = args.joinToString(" ")

        val onlineOperators = plugin.server.onlinePlayers.filter { it.isOp }

        if (onlineOperators.isEmpty()) {
            sender.sendMessage("${ChatColor.RED}No operators online right now.")
        } else {
            onlineOperators.forEach {
                it.sendMessage("${ChatColor.RED}${sender.displayName} asks > ${ChatColor.YELLOW}$message")
            }

            sender.sendMessage("You asked operators > ${ChatColor.YELLOW}$message")
        }

        return true
    }

}