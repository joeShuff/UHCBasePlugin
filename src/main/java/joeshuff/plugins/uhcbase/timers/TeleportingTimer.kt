package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.base.GenerateLocationsCommand
import joeshuff.plugins.uhcbase.utils.getPlayingWorlds

import org.bukkit.ChatColor
import org.bukkit.Difficulty
import org.bukkit.scheduler.BukkitRunnable
import java.lang.Integer.min

class TeleportingTimer(val game: UHC, val locations: ArrayList<GenerateLocationsCommand.PlayerDestination>): BukkitRunnable() {
    val plugin = game.plugin

    val groupAmount = game.configController.TELEPORT_SIZE.get()

    var seconds = -1

    var amountTeleportingThisTime = 0

    var TELEPORT_DELAY = 4

    init {
        TELEPORT_DELAY = game.configController.TELEPORT_DELAY.get()

        plugin.server.broadcastMessage("${ChatColor.GREEN}Teleporting ${locations.size} players in groups of $groupAmount")

        game.getPlayingWorlds().forEach { it.difficulty = Difficulty.PEACEFUL }
    }

    override fun run() {
        when (seconds) {
            0 -> {
                amountTeleportingThisTime = min(locations.size, groupAmount)
                plugin.server.broadcastMessage("${ChatColor.YELLOW}Prepping teleport for ${ChatColor.RED}$amountTeleportingThisTime ${if (amountTeleportingThisTime == 1) "person" else "people"}.")

                (0 until amountTeleportingThisTime).forEach {
                    val thisTeleportation = locations.removeFirstOrNull()

                    thisTeleportation?.player?.let {
                        it.teleport(thisTeleportation.location)
                        plugin.server.broadcastMessage("${ChatColor.YELLOW}${it.name}${ChatColor.WHITE} has been teleported")
                    }

                    if (locations.isEmpty()) {
                        plugin.server.broadcastMessage("${ChatColor.GREEN}All players have been teleported");
                        this.cancel()
                        return
                    }
                }
            }
            TELEPORT_DELAY -> { seconds = -1 }
        }

        plugin.logger.info("Teleport delay $seconds")
        seconds ++
    }


}