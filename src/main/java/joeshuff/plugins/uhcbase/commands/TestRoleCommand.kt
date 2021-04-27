package joeshuff.plugins.uhcbase.commands

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class TestRoleCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return command.notifyInvalidPermissions(sender, "This command is for players only.")
        }

        if (game.isSpectator(sender)) sender.sendMessage("You are a spectator.")
        if (game.isContestant(sender)) sender.sendMessage("You are a contestant.")

        return true
    }

}