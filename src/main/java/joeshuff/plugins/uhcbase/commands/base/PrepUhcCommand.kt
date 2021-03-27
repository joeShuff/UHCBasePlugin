package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.PositionsController
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType

class PrepUhcCommand(val plugin: JavaPlugin): CommandExecutor {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {

        val uhcPlugin = plugin as UHCBase

        if (sender is Player) {
            if (!sender.isOp) {
                sender.sendMessage("${ChatColor.RED}You do not have permissions to use this command.")
                return true
            }
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        val world = Bukkit.getWorld(Constants.UHCWorldName)

        if (world == null) {
            sender.sendMessage("${ChatColor.RED}Unable to find UHC world : " + Constants.UHCWorldName)
            return true
        }

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        if (uhcPlugin.UHCPrepped) {
            sender.sendMessage(ChatColor.RED.toString() + "World already prepped!")
            return true
        }

        uhcPlugin.UHCPrepped = true

        val worldBorderDiameter = Integer.valueOf(args[0])
        val worldCenter = Location(world, 0.0, (world.getHighestBlockAt(0, 0).y + 1).toDouble(), 0.0)

        plugin.server.onlinePlayers.forEach {
            it.teleport(worldCenter)

            it.inventory.clear()
            it.health = 20.0
            it.foodLevel = 20
            it.enderChest.clear()

            it.exp = 0f
            it.level = 0

            it.gameMode = GameMode.SURVIVAL

            it.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 200, 100))
            it.addPotionEffect(PotionEffect(PotionEffectType.SATURATION, 10, 10))
            it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 1000000, 100))
            it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 100))
            it.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 1000000, -100))
            it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 256))
        }

        plugin.getPlayingWorlds().forEach {
            it.worldBorder.center = worldCenter
            it.worldBorder.size = worldBorderDiameter.toDouble()
            it.worldBorder.warningDistance = 25
            it.time = 0

            it.setGameRule(GameRule.NATURAL_REGENERATION, false)
            it.pvp = false
        }

        plugin.server.broadcastMessage("${ChatColor.GREEN}World border set to $worldBorderDiameter blocks diameter.")

        scoreboard.getObjective("health1")?.let { it.unregister() }
        scoreboard.getObjective("health2")?.let { it.unregister() }
        scoreboard.getObjective("kills")?.let { it.unregister() }

        scoreboard.registerNewObjective("health1", "health", "listhealth", RenderType.HEARTS).displaySlot = DisplaySlot.PLAYER_LIST
        scoreboard.registerNewObjective("health2", "health", "${ChatColor.RED}♥").displaySlot = DisplaySlot.BELOW_NAME
        scoreboard.registerNewObjective("kills", "stat.playerKills", "${ChatColor.RED}-- Kills --").displaySlot = DisplaySlot.SIDEBAR

        val teams = plugin.getConfigController().TEAMS.get()

        plugin.server.dispatchCommand(plugin.server.consoleSender, "loc $teams")

        return true
    }

}