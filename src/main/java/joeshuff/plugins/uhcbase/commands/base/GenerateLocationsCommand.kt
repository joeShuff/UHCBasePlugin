package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import joeshuff.plugins.uhcbase.timers.TeleportingTimer
import joeshuff.plugins.uhcbase.utils.getOnlineTeams
import joeshuff.plugins.uhcbase.utils.getPlayingWorlds
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Difficulty
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.scoreboard.Team
import java.util.*
import kotlin.math.roundToInt

class GenerateLocationsCommand(val game: UHC): CommandExecutor {

    val plugin = game.plugin

    class PlayerDestination(val player: Player, val location: Location)

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        Bukkit.getWorld(Constants.UHCWorldName)?: run {
            sender.sendMessage("${ChatColor.RED}UHC World called '${Constants.UHCWorldName}' does not exist")
            return true
        }

        if (args.isEmpty()) return command.notifyCorrectUsage(sender)

        game.getPlayingWorlds().forEach {
            it.difficulty = Difficulty.PEACEFUL
        }

        val arg = args[0].toLowerCase()
        if (arg != "true" && arg != "false") {
            return command.notifyCorrectUsage(sender)
        }

        var respectTeams = arg == "true"

        val maxXZCoord = (plugin.server.getWorld(Constants.UHCWorldName)?.worldBorder?.size?.roundToInt()?: 100) / 2
        val minSpread = maxXZCoord / 6

        val scoreboard = plugin.server.scoreboardManager?.mainScoreboard

        if (scoreboard == null) {
            sender.sendMessage("${ChatColor.RED}Something went wrong obtaining the scoreboard")
            return true
        }

        val amountOfLocations = (if (respectTeams) game.getOnlineTeams() else game.getContestants()).size

        if (amountOfLocations == 0) {
            plugin.server.broadcastMessage("${ChatColor.RED}Seems only 0 locations want to generate. use /loc to retry")
            return true
        }

        plugin.server.broadcastMessage("${ChatColor.GOLD}Generating ${ChatColor.RED}$amountOfLocations ${ChatColor.GOLD}locations.")

        val generatedLocations = checkLocs(minSpread, maxXZCoord, amountOfLocations)

        val playerLocations = arrayListOf<PlayerDestination>()

        (if (respectTeams) game.getOnlineTeams() else game.getContestants()).forEach {
            val thisLocation = generatedLocations.removeAt(0)

            if (it is Team) {
                it.entries.mapNotNull { Bukkit.getPlayer(it) }.forEach {
                    playerLocations.add(PlayerDestination(it, thisLocation))
                }
            } else if (it is Player) {
                playerLocations.add(PlayerDestination(it, thisLocation))
            }
        }

        plugin.server.broadcastMessage("${ChatColor.GOLD}Generated Locations")
        plugin.server.broadcastMessage("${ChatColor.GREEN}Beginning teleportations...")
        TeleportingTimer(game, playerLocations).runTaskTimer(plugin, 40, 20)

        return true
    }

    private fun validTopLocation(location: Location): Boolean {
        val highestBlock = location.world?.getHighestBlockAt(location)

        highestBlock?.let {
            return !it.isLiquid
        }

        return false
    }

    private fun checkLocs(minSpread: Int, maxXZCoord: Int, amountOfLocations: Int): ArrayList<Location> {
        val locations = arrayListOf<Location>()

        for (locNumber in 0 until amountOfLocations) {
            var validLoc = false
            val rnd = Random()
            var genLoc: Location
            var iterations = 0

            do {
                iterations++
                validLoc = true

                var x = rnd.nextInt(maxXZCoord - 5) + 1
                var z = rnd.nextInt(maxXZCoord - 5) + 1

                if (rnd.nextBoolean()) x *= -1
                if (rnd.nextBoolean()) z *= -1

                var y = (Bukkit.getWorld(Constants.UHCWorldName)?.getHighestBlockYAt(x, z)?: -1)

                genLoc = Location(Bukkit.getWorld(Constants.UHCWorldName), x.toDouble(), (y + 4).toDouble(), z.toDouble())

                if (iterations > 20 && validTopLocation(genLoc)) {
                    plugin.logger.info("Forced generation of location")
                    validLoc = true
                } else {
                    locations.forEach {
                        if ((genLoc.distance(it).toInt()) < minSpread) {
                            validLoc = false
                        }
                    }

                    if (!validTopLocation(genLoc)) {
                        validLoc = false
                    }
                }
            } while (!validLoc)

            locations.add(genLoc)
        }

        return locations
    }

}