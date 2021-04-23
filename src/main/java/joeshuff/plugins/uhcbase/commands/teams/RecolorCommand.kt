package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.utils.recolorAllTeams
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class RecolorCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        recolorAllTeams(plugin)

        sender.sendMessage("${ChatColor.GREEN}Team colors have been regenerated.")

        return true
    }

}