package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHCBase
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StopPregenerationCommand(val plugin: UHCBase): CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}This command is for operators only")
                return true
            }
        }

        plugin.ongoingPregenerationTimer?.let {
            it.abortGeneration = true
        }?: run {
            sender.sendMessage("${ChatColor.RED}There is not an ongoing pregeneration task to stop")
        }

        return true
    }
}