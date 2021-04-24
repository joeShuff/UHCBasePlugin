package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class WorldTestCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        val UHCtest = Bukkit.getWorld(Constants.UHCWorldName)

        if (UHCtest == null) {
            sender.sendMessage("${ChatColor.RED}Unable to find the UHC world : ${Constants.UHCWorldName}")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Successfully found the UHC world : ${Constants.UHCWorldName}")
        }

        val hubtest = Bukkit.getWorld(Constants.hubWorldName)

        if (hubtest == null) {
            sender.sendMessage("${ChatColor.RED}Unable to find the hub world : ${Constants.hubWorldName}")
        } else {
            sender.sendMessage("${ChatColor.GREEN}Successfully found the hub world : ${Constants.hubWorldName}")
        }

        return true
    }

}