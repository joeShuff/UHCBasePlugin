package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.timers.PregenerationTimer
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PregenerateChunksCommand(val plugin: UHCBase): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}This command is for operators only")
                return true
            }
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        plugin.ongoingPregenerationTimer?.let {
            sender.sendMessage("${ChatColor.RED}There is currently an ongoing pregeneration task")
            return true
        }

        plugin.server.getWorld(Constants.UHCWorldName)?.let {
            plugin.ongoingPregenerationTimer = PregenerationTimer(plugin, args[0].toInt())
        }?: run {
            sender.sendMessage("${ChatColor.RED}Cannot find UHC world ${ChatColor.ITALIC}${Constants.UHCWorldName}")
            return true
        }

        return true
    }
}