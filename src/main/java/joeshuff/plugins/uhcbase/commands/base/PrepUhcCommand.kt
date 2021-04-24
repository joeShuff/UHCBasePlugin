package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.utils.*
import org.apache.commons.lang.WordUtils
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType

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

        val teams = plugin.getConfigController().TEAMS.get()

        if (teams && getOnlineTeams().isEmpty()) {
            sender.sendMessage("${ChatColor.RED}The game is configured to use teams but there are no teams. Either set teams to false or generate some teams.")
            return true
        }

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

        game.gameState.onNext(UHC.GAME_STATE.PREPPED)

        val worldBorderDiameter = Integer.valueOf(args[0])
        val worldCenter = Location(world, 0.0, (world.getHighestBlockAt(0, 0).y + 1).toDouble(), 0.0)

        plugin.server.onlinePlayers.forEach {
            it.teleport(worldCenter)

            it.removeAllAdvancements()

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

        plugin.setupScoreboard()

        plugin.server.broadcastMessage("${ChatColor.GREEN}World border set to $worldBorderDiameter blocks diameter.")

        plugin.server.dispatchCommand(plugin.server.consoleSender, "loc $teams")

        return true
    }

}