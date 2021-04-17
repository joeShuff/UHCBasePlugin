package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.PermissionDefault
import org.bukkit.plugin.java.JavaPlugin

class StopUhcCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val uhcPlugin = plugin as UHCBase

        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        uhcPlugin.UHCLive = false
        uhcPlugin.UHCPrepped = false

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        scoreboard.getObjective("health1")?.let { it.unregister() }
        scoreboard.getObjective("health2")?.let { it.unregister() }
        scoreboard.getObjective("kills")?.let { it.unregister() }

        scoreboard.teams.forEach { it.unregister() }

        plugin.server.onlinePlayers.forEach { player ->
            player.inventory.clear()
            player.health = 20.0
            player.foodLevel = 20
            player.enderChest.clear()

            player.gameMode = GameMode.SURVIVAL

            player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

            player.sendDefaultTabInfo(plugin)
        }

//        Season14.nominated = ArrayList()

        plugin.liveGameListener?.stop()

        plugin.getServer().pluginManager.getPermission("blockBefore.allowed")?.default = PermissionDefault.FALSE

        return true
    }

}