package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.timers.PregenerationTimer
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class PregenerateChunksCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        game.ongoingPregenerationTimer?.let {
            sender.sendMessage("${ChatColor.RED}There is currently an ongoing pregeneration task")
            return true
        }

        plugin.server.getWorld(Constants.UHCWorldName)?.let {
            game.ongoingPregenerationTimer = PregenerationTimer(game, args[0].toInt())
        }?: run {
            sender.sendMessage("${ChatColor.RED}Cannot find UHC world ${ChatColor.ITALIC}${Constants.UHCWorldName}")
            return true
        }

        return true
    }
}