package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.PositionsController
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.listeners.PlayerListener
import joeshuff.plugins.uhcbase.timers.GameTimer
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartUhcCommand(val plugin: UHCBase): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        if (!plugin.UHCPrepped) {
            sender.sendMessage("${ChatColor.RED}UHC world has not been prepped. Do /prepuhc")
            return true
        }

        if (plugin.UHCLive) {
            sender.sendMessage("${ChatColor.RED}UHC is already in progress")
            return true
        }

        plugin.UHCLive = true

        plugin.positionsController = PositionsController(plugin, plugin.getConfigController().TEAMS.get())

        GameTimer(plugin).runTaskTimer(plugin, 0, 20);

        return true
    }

}