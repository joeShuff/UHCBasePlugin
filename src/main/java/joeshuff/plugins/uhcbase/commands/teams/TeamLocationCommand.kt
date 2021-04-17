package joeshuff.plugins.uhcbase.commands.teams

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class TeamLocationCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}This command is for players only")
            return true
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        scoreboard.getEntryTeam(sender.name)?.let { team ->
            val senderLocation = sender.location
            var message = "${ChatColor.GREEN}[TEAM] ${sender.displayName}${ChatColor.GRAY}> ${ChatColor.WHITE}X: ${senderLocation.blockX} Y: ${senderLocation.blockY} Z: ${senderLocation.blockZ}"

            team.entries.forEach {entryName ->
                plugin.server.getPlayer(entryName)?.let {teammate ->
                    if (teammate.displayName == sender.displayName) {
                        teammate.sendMessage("$message")
                    }
                    else if (teammate.location.world?.name == senderLocation.world?.name) {
                        val distance = teammate.location.distance(senderLocation).toInt()
                        //TODO: MAYBE LOCALLY GLOW THE SENDER?
                        teammate.sendMessage("$message ($distance blocks)")
                    }
                    else {
                        teammate.sendMessage("$message (${senderLocation.world?.name?: "Different world"})")
                    }
                }
            }
        }?: run {
            sender.sendMessage("${ChatColor.RED}You are not part of a team!")
        }

        return true
    }

}