package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHCBase
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

class TeleportingTimer(val plugin: UHCBase, val respectTeams: Boolean, val locations: List<Location>): BukkitRunnable() {

    val teams = arrayListOf<Team>()
    val players = arrayListOf<Player>()

    var seconds = 1
    var currentSlot = 0

    init {
        teams.addAll(TeamsUtils.getOnlineTeams())
        players.addAll(plugin.server.onlinePlayers)

        players.forEach {
            plugin.liveGameListener?.playingList?.add(it.name)
        }

        plugin.getPlayingWorlds().forEach { it.difficulty = Difficulty.PEACEFUL }
    }

    override fun run() {

        plugin.logger.info("slot is $currentSlot and there are ${locations.size} locations")

        if (currentSlot >= locations.size) {
            plugin.server.broadcastMessage("${ChatColor.GREEN}All players have been teleported");
            this.cancel()
            return
        }

        when (seconds) {
            1 -> {
                val preppingFor = if (respectTeams) teams[currentSlot].displayName else players[currentSlot].displayName
                plugin.server.broadcastMessage("${ChatColor.YELLOW}Prepping teleport for ${ChatColor.RED}$preppingFor")
            }
            2 -> {
                plugin.server.broadcastMessage("${ChatColor.GRAY}Loading chunks...")
                val thisLocation = locations[currentSlot]
                val thisChunk = thisLocation.chunk

                var successfullyLoadedAll = true

                for (xOffset in -2..2) {
                    for (zOffset in -2..2) {
                        if (thisLocation.world?.loadChunk(thisChunk.x + xOffset, thisChunk.z + zOffset, true) != true) {
                            successfullyLoadedAll = false
                        }
                    }
                }

                if (!successfullyLoadedAll) {
                    plugin.server.broadcastMessage("${ChatColor.RED}${ChatColor.ITALIC}Something went wrong loading chunks...")
                }
            }
            8 -> {

                val playersToTp = arrayListOf<Player>()

                if (respectTeams) {
                    teams[currentSlot].entries.forEach {entry ->
                        Bukkit.getPlayer(entry)?.let { player ->
                            playersToTp.add(player)
                        }
                    }
                } else {
                    playersToTp.add(players[currentSlot])
                }

                playersToTp.forEach {
                    it.teleport(locations[currentSlot])
                    it.addPotionEffect(PotionEffect(PotionEffectType.BLINDNESS, 1000000, 100))
                    it.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 1000000, 100))
                    it.addPotionEffect(PotionEffect(PotionEffectType.JUMP, 1000000, -100))
                    it.addPotionEffect(PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1000000, 256))
                    plugin.server.broadcastMessage("${ChatColor.YELLOW}${it.name}${ChatColor.WHITE} has been teleported")
                }

                seconds = 0
                currentSlot++
            }
        }

        seconds ++
    }


}