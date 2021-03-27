package joeshuff.plugins.uhcbase.commands.base

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.Objective
import org.bukkit.scoreboard.Scoreboard

class ShowKillsCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val killsTest: Objective? = scoreboard.getObjective("uhckills")

        killsTest?.let {
            it.displaySlot == DisplaySlot.SIDEBAR
            sender.sendMessage("${ChatColor.GREEN}Successfully set the kills to sidebar")
        }?: run {
            sender.sendMessage("${ChatColor.RED}You need to prep the world for UHC first. Use /prepuhc")
        }

        return true
    }

}