package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.utils.updatePlayerFlight
import joeshuff.plugins.uhcbase.utils.updateVisibility
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class SpectatorCommand(val game: UHC): TabExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        if (game.state != UHC.GAME_STATE.PRE_GAME) {
            sender.sendMessage("${ChatColor.RED}Cannot execute this command when not in pregame")
            return true
        }

        Bukkit.getOnlinePlayers().firstOrNull { it.name.toLowerCase() == args[0].toLowerCase() }?.let {
            if (game.spectatorList.contains(it.uniqueId.toString())) {
                game.spectatorList.remove(it.uniqueId.toString())
                sender.sendMessage("${ChatColor.RED}Removed ${it.name} from the spectator list.")
                it.sendMessage("${ChatColor.RED}You are no longer a spectator")
            } else {
                game.spectatorList.add(it.uniqueId.toString())
                sender.sendMessage("${ChatColor.GREEN}Added ${it.name} to the spectator list.")
                it.sendMessage("${ChatColor.GREEN}You are now a spectator")
            }
        }?: run {
            sender.sendMessage("${ChatColor.RED}Cannot find player with name ${args[0]}")
        }

        game.updatePlayerFlight()
        game.updateVisibility()

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender is Player && !sender.isOp) return emptyList()

        when (args[0].length) {
            0 -> return Bukkit.getOnlinePlayers().map { it.name }
        }

        return emptyList()
    }

}