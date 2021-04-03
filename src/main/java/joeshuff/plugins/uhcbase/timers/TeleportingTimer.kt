package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.commands.base.GenerateLocationsCommand
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.utils.TeamsUtils
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Difficulty
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.scoreboard.Team
import java.lang.Integer.min

class TeleportingTimer(val plugin: UHCBase, val locations: ArrayList<GenerateLocationsCommand.PlayerDestination>): BukkitRunnable() {
    val groupAmount = plugin.getConfigController().TELEPORT_SIZE.get()

    var seconds = 1

    var amountTeleportingThisTime = 0

    var TELEPORT_DELAY = 4

    init {
        TELEPORT_DELAY = plugin.getConfigController().TELEPORT_DELAY.get()

        plugin.server.broadcastMessage("${ChatColor.GREEN}Teleporting ${locations.size} players in groups of $groupAmount")

        plugin.getPlayingWorlds().forEach { it.difficulty = Difficulty.PEACEFUL }
    }

    override fun run() {
        if (locations.isEmpty()) {
            plugin.server.broadcastMessage("${ChatColor.GREEN}All players have been teleported");
            this.cancel()
            return
        }

        when (seconds) {
            0 -> {
                amountTeleportingThisTime = min(locations.size, groupAmount)
                plugin.server.broadcastMessage("${ChatColor.YELLOW}Prepping teleport for ${ChatColor.RED}$amountTeleportingThisTime people.")

                (0..amountTeleportingThisTime).forEach {
                    val thisTeleportation = locations.removeAt(0)

                    with (thisTeleportation.player) {
                        teleport(thisTeleportation.location)
                        plugin.server.broadcastMessage("${ChatColor.YELLOW}${name}${ChatColor.WHITE} has been teleported")
                    }
                }
            }
            TELEPORT_DELAY -> { seconds = 0 }
        }

        seconds ++
    }


}