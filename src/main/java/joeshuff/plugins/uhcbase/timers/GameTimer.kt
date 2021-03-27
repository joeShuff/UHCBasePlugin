package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.listeners.PlayerListener
import joeshuff.plugins.uhcbase.utils.TeamsUtils
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import joeshuff.plugins.uhcbase.utils.showRules
import org.bukkit.*
import org.bukkit.permissions.PermissionDefault
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class GameTimer(
        val plugin: UHCBase
): BukkitRunnable() {

    val permaDay: Boolean = plugin.config.getBoolean("perma-day")
    val permaEp: Int = plugin.config.getInt("perma-day-ep")

    val shrink: Boolean = plugin.config.getBoolean("should-shrink")
    val shrinkSize: Int = plugin.config.getInt("shrink-size")
    val shrinkEp: Int = plugin.config.getInt("shrink-ep")
    val shrinkLength: Int = plugin.config.getInt("shrink-time")

    val graceEnabled: Boolean = plugin.config.getBoolean("grace-period")
    val graceEndEpisode: Int = plugin.config.getInt("grace-end-episode")

    val episodeTime: Int = plugin.config.getInt("episode-length")

    val episodesEnabled = plugin.getConfigController().EPISODES_ENABLED.get()

    init {
        plugin.server.broadcastMessage("UHC Started${if (episodesEnabled) " with Episode length of $episodeTime minute(s)" else ""}")

        val shrinkAt = if (episodesEnabled) {
            "Episode $shrinkEp"
        } else {
            "${shrinkEp * episodeTime} minutes"
        }

        plugin.server.broadcastMessage("Shrink at $shrinkAt to ${shrinkSize}x${shrinkSize} and will last $shrinkLength minutes")

        plugin.server.onlinePlayers.forEach {
            it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 10f)
        }
    }

    var episodeNumber = 1

    var effects = Arrays.asList(PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false), PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1, false, false), PotionEffect(PotionEffectType.JUMP, 1000000, 2, false, false), PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4, false, false), PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0, false, false))

    var startSeconds = 10

    var seconds = 0
    var minutes = 0

    fun getFormattedGameTime(): String {
        if (plugin.getConfigController().EPISODES_ENABLED.get()) {
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            return "${(((episodeNumber - 1) * episodeTime) + minutes).toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
    }

    fun getFormattedRemaining(): String {
        plugin.positionsController?.let {
            return it.stillAlive()
        }?: return ""
    }

    fun formatTabHeaderFooter(input: String): String {
        return input
                .replace("{gameTime}", getFormattedGameTime())
                .replace("{episode}", "$episodeNumber")
                .replace("{remaining}", getFormattedRemaining())
    }

    override fun run() {
        //TODO: PLUGIN TICK (Season14)

        if (!plugin.UHCLive) {
            Bukkit.getServer().broadcastMessage(
                    "${ChatColor.DARK_RED}===============================\n" +
                    "${ChatColor.RED}${ChatColor.BOLD}THE UHC HAS BEEN STOPPED!${ChatColor.RESET}${ChatColor.DARK_RED}\n" +
                    "===============================")

            val gameOverMessage = plugin.getConfigController().loadConfigFile("customize")?.get("end_game_title")?: "CUSTOM UHC"

            Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "====== " + gameOverMessage + ChatColor.GOLD + " ======")
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "    1st Place: " + plugin.positionsController?.firstPlace)
            Bukkit.getServer().broadcastMessage(ChatColor.GRAY.toString() + "    2nd Place: " + plugin.positionsController?.secondPlace)
            Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED.toString() + "    3rd Place: " + plugin.positionsController?.thirdPlace)
            Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "===============================")

            plugin.server.onlinePlayers.forEach {
                it.sendDefaultTabInfo(plugin)
                it.inventory.clear()
                it.enderChest.clear()

                plugin.server.onlinePlayers.forEach { otherPlayer ->
                    otherPlayer.showPlayer(plugin, it)
                }
            }

            this.cancel()
            return
        }

        if (startSeconds > -1) {
            val color = when {
                startSeconds >= 6 -> ChatColor.DARK_RED
                startSeconds >= 4 -> ChatColor.RED
                startSeconds >= 2 -> ChatColor.GOLD
                else -> ChatColor.YELLOW
            }

            plugin.server.onlinePlayers.forEach {
                it.sendTitle("", "$color$startSeconds", 10, 70, 20)
                it.playSound(it.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
            }

            startSeconds --

            if (startSeconds == -1) {
                episodeNumber = 1

                plugin.server.setWhitelist(true)

                plugin.server.onlinePlayers.forEach {player ->
                    player.isWhitelisted = true

                    if (plugin.getConfigController().EPISODES_ENABLED.get()) {
                        player.sendTitle("§9EPISODE §a$episodeNumber §9MARKER", "", 10, 70, 20)
                    }

                    player.showRules(plugin)

                    player.isOp = false
                    player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.2f, 1f)
                    player.health = 20.0
                    player.foodLevel = 20

                    player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

                    //TODO: SUPER POWERS GAME MODE HERE
//                    player.addPotionEffect(effects.random())
                    player.damage(0.5)
                    player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 10))

                    plugin.server.pluginManager.getPermission("blockBefore.allowed")?.default = PermissionDefault.TRUE

                    val graceEnabled = plugin.getConfigController().GRACE_PERIOD.get()

                    plugin.getPlayingWorlds().forEach {
                        it.difficulty = Difficulty.HARD
                        it.time = 0
                        it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true)

                        if (graceEnabled) it.pvp = false
                    }

                    //TODO: REMOVE ALL ADVANCEMENTS
                }

                plugin.livePlayerListener = PlayerListener(plugin)
            }
        }
        else {
            seconds++

            if (seconds == 60) {
                minutes ++
                seconds = 0

                if (minutes == episodeTime) {
                    minutes = 0
                    episodeNumber ++

                    //TODO: MOLES GEN COMMAND

                    //TODO: GENERATE TEAMS SEASON 13

                    if (shrink && shrinkEp == episodeNumber) {
                        plugin.server.broadcastMessage(ChatColor.GOLD.toString() + "======================")
                        plugin.server.broadcastMessage(ChatColor.RED.toString() + "WORLD BORDER SHRINKING TO " + ChatColor.GREEN + shrinkSize + "x" + shrinkSize + " over " + shrinkLength + " minute(s)!")
                        plugin.server.broadcastMessage(ChatColor.GOLD.toString() + "======================")

                        plugin.getPlayingWorlds().forEach {
                            it.worldBorder.setSize(shrinkSize.toDouble(), (shrinkLength * 60).toLong())
                        }
                    }

                    var subtitle = ""
                    val title = if (plugin.getConfigController().EPISODES_ENABLED.get()) "§9EPISODE §a$episodeNumber §9MARKER" else ""

                    if (graceEnabled && graceEndEpisode == episodeNumber) {
                        plugin.server.broadcastMessage("${ChatColor.YELLOW}PVP has been ${ChatColor.GREEN.toString() + ChatColor.BOLD.toString()}ENABLED")
                    }

                    if (permaDay && permaEp == episodeNumber) {
                        plugin.server.broadcastMessage("${ChatColor.GREEN}Perma-Day ${ChatColor.YELLOW}has been enabled.")
                    }

                    plugin.getPlayingWorlds().forEach {
                        if (graceEnabled && graceEndEpisode == episodeNumber) {
                            it.pvp = true
                            plugin.server.broadcastMessage("${ChatColor.YELLOW}PVP has been ${ChatColor.GREEN.toString() + ChatColor.BOLD.toString()}ENABLED")
                        }

                        if (permaDay && permaEp == episodeNumber) {
                            it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)

                            plugin.getPlayingWorlds().forEach { it.time = 0 }

                            plugin.server.broadcastMessage("§aPERMA-DAY ENABLED")
                        }
                    }

                    plugin.server.onlinePlayers.forEach {
                        if (plugin.getConfigController().EPISODES_ENABLED.get()) {
                            it.playSound(it.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
                        }

                        it.sendTitle(title, subtitle, 10, 70, 20)
                    }
                }
            }

            plugin.getConfigController().loadConfigFile("customize")?.let {
                var header = it.getString("in_game_tab_header")?: ""
                var footer = it.getString("in_game_tab_footer")?: ""

                header = formatTabHeaderFooter(header)
                footer = formatTabHeaderFooter(footer)

                plugin.server.onlinePlayers.forEach { it.setPlayerListHeaderFooter(header, footer) }
            }
        }
    }

}