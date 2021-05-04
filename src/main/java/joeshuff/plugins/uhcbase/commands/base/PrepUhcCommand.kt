package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.utils.*
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class PrepUhcCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        if (game.state != UHC.GAME_STATE.PRE_GAME) {
            sender.sendMessage(ChatColor.RED.toString() + "World already prepped!")
            return true
        }

        val teams = game.configController.TEAMS.get()
        if (teams && game.getOnlineTeams().isEmpty()) {
            sender.sendMessage("${ChatColor.RED}The game is configured to use teams but there are no teams. Either set teams to false or generate some teams.")
            return true
        }

        val world = Bukkit.getWorld(Constants.UHCWorldName)

        if (world == null) {
            sender.sendMessage("${ChatColor.RED}Unable to find UHC world: " + Constants.UHCWorldName)
            return true
        }

        if (args.size == 1) {
            args[0].toIntOrNull()?.let {
                game.configController.BORDER_SIZE.set(it)
            }?: run {
                sender.sendMessage("${ChatColor.RED}Unable to set world border diameter to: ${ChatColor.YELLOW}${Constants.UHCWorldName}")
                return true
            }
        }

        game.gameState.onNext(UHC.GAME_STATE.PREPPING)

        return true
    }

}