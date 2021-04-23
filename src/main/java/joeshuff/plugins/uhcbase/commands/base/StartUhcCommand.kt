package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class StartUhcCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (game.state == UHC.GAME_STATE.PRE_GAME) {
            sender.sendMessage("${ChatColor.RED}UHC world has not been prepped. Do /prepuhc")
            return true
        }

        if (game.state != UHC.GAME_STATE.PREPPED) {
            sender.sendMessage("${ChatColor.RED}UHC is already in progress")
            return true
        }

        game.gameState.onNext(UHC.GAME_STATE.IN_GAME)

        return true
    }

}