package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WorldTpCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        } else {
            return command.notifyInvalidPermissions(sender, "This command is for players only.")
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