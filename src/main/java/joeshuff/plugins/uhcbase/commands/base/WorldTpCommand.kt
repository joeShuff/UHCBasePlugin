package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WorldTpCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}This command is for operators only")
                return true
            }
        } else {
            sender.sendMessage("This command is for players only")
            return true
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        Bukkit.getWorld(args[0])?.let {
            (sender as Player).teleport(Location(it, sender.location.x, sender.location.y, sender.location.z))
        }?: run {
            sender.sendMessage("${ChatColor.RED}Can't find world for ${args[0]}")
        }

        return true
    }

}