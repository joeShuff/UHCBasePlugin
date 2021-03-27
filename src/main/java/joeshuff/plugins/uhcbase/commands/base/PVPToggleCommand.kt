package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PVPToggleCommand(val plugin: JavaPlugin): TabExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        var pvpSet: Boolean

        if (args[0].toLowerCase() == "on") {
            pvpSet = true
            sender.sendMessage("${ChatColor.GREEN}PVP Activated")
        } else if (args[0].toLowerCase() == "off") {
            pvpSet = false
            sender.sendMessage("${ChatColor.RED}PVP Deactivated")
        } else {
            return command.notifyCorrectUsage(sender)
        }

        plugin.getPlayingWorlds().forEach { it.pvp = pvpSet }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        when (args[0].length) {
            0 -> return listOf("on", "off")
        }

        return emptyList()
    }

}