package joeshuff.plugins.uhcbase.commands.teams

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.utils.createTeamFor
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CreateTeamCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val firstPlayer = args[0]

        val newTeam = createTeamFor(scoreboard, firstPlayer)

        args.forEach {playerToAdd ->
            newTeam.addEntry(playerToAdd)

            plugin.server.getPlayer(playerToAdd)?.let {
                it.sendMessage("${ChatColor.GREEN}You have joined ${newTeam.displayName}")
            }
        }

        sender.sendMessage("${ChatColor.GREEN}Team ${newTeam.displayName} created with ${newTeam.entries.size} players")

        return true
    }
}