package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.utils.getPlayingWorlds
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class PVPToggleCommand(val game: UHC): TabExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
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
        if (sender is Player && !sender.isOp) return emptyList()

        when (args[0].length) {
            0 -> return listOf("on", "off")
        }

        return emptyList()
    }

}