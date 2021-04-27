package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.listeners.GameListener
import joeshuff.plugins.uhcbase.utils.getHubSpawnLocation
import joeshuff.plugins.uhcbase.utils.getPlayingWorlds
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import joeshuff.plugins.uhcbase.utils.showRules
import org.bukkit.*
import org.bukkit.permissions.PermissionDefault
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scheduler.BukkitRunnable

class GameTimer(val game: UHC): BukkitRunnable() {

    val plugin = game.plugin

    private val permaEp: Int = game.configController.getIntFromConfig("perma-day-ep")?: 0

    private val shrinkEp: Int = game.configController.getIntFromConfig("shrink-ep")?: 0

    private val shrinkSize: Int = game.configController.getIntFromConfig("shrink-size")?: 100

    private val shrinkLength: Int = game.configController.getIntFromConfig("shrink-time")?: 20

    private val graceEndEpisode: Int = game.configController.getIntFromConfig("grace-end-episode")?: 0

    private val episodeTime: Int = game.configController.getIntFromConfig("episode-length")?: 20

    private val episodesEnabled = game.configController.EPISODES_ENABLED.get()

    init {
        plugin.server.broadcastMessage("UHC Started${if (episodesEnabled) " with Episode length of $episodeTime minute(s)" else ""}")

        if (shrinkEp > 0) {
            val shrinkAt = if (episodesEnabled) {
                "Episode $shrinkEp"
            } else {
                "${shrinkEp * episodeTime} minutes"
            }

            plugin.server.broadcastMessage("Shrink at $shrinkAt to ${shrinkSize}x${shrinkSize} and will last $shrinkLength minutes")
        }

        game.getAllPlayers().forEach {
            it.playSound(it.location, Sound.ENTITY_PLAYER_LEVELUP, 1f, 10f)
        }

        game.plugin.disposables.add(game.gameState
            .distinctUntilChanged()
            .subscribe {
                if (it == UHC.GAME_STATE.VICTORY_LAP) {
                    onUHCStop()
                }
            }
        )
    }

    var episodeNumber = 1

    var startSeconds = 10

    var seconds = 0
    var minutes = 0

    fun getFormattedGameTime(): String {
        if (game.configController.EPISODES_ENABLED.get()) {
            return "${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        } else {
            return "${(((episodeNumber - 1) * episodeTime) + minutes).toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}"
        }
    }

    fun getFormattedRemaining(): String {
        game.positionsController?.let {
            return it.stillAlive()
        }?: return ""
    }

    fun formatTabHeaderFooter(input: String): String {
        return input
                .replace("{gameTime}", getFormattedGameTime())
                .replace("{episode}", "$episodeNumber")
                .replace("{remaining}", getFormattedRemaining())
    }

    fun onEpisodeChange(episode: Int) {

        game.gamemodes.forEach { it.onEpisodeChange(episode) }

        if (shrinkEp == episode) {
            plugin.server.broadcastMessage(ChatColor.GOLD.toString() + "======================")
            plugin.server.broadcastMessage(ChatColor.RED.toString() + "WORLD BORDER SHRINKING TO " + ChatColor.GREEN + shrinkSize + "x" + shrinkSize + " over " + shrinkLength + " minute(s)!")
            plugin.server.broadcastMessage(ChatColor.GOLD.toString() + "======================")

            game.getPlayingWorlds().forEach {
                it.worldBorder.setSize(shrinkSize.toDouble(), (shrinkLength * 60).toLong())
            }
        }

        var subtitle = ""
        val title = if (game.configController.EPISODES_ENABLED.get()) "§9EPISODE §a$episodeNumber §9MARKER" else ""

        if (graceEndEpisode == episode) {
            plugin.server.broadcastMessage("${ChatColor.YELLOW}PVP has been ${ChatColor.GREEN.toString() + ChatColor.BOLD.toString()}ENABLED")
        }

        if (permaEp == episode) {
            plugin.server.broadcastMessage("${ChatColor.GREEN}Perma-Day ${ChatColor.YELLOW}has been enabled.")
        }

        game.getPlayingWorlds().forEach {
            if (graceEndEpisode == episode) {
                it.pvp = true
            }

            if (permaEp == episode) {
                it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
                it.time = 0
            }
        }

        game.getAllPlayers().forEach {
            if (game.configController.EPISODES_ENABLED.get()) {
                it.playSound(it.location, Sound.BLOCK_ANVIL_LAND, 1f, 1f)
            }

            it.sendTitle(title, subtitle, 10, 70, 20)
        }
    }

    fun onUHCStop() {
        Bukkit.getServer().broadcastMessage("${ChatColor.DARK_RED}===============================\n${ChatColor.RED}${ChatColor.BOLD}THE UHC HAS BEEN STOPPED!${ChatColor.RESET}${ChatColor.DARK_RED}\n===============================")

        val gameOverMessage = game.configController.loadConfigFile("customize")?.get("end_game_title")?: "CUSTOM UHC"

        Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "====== " + gameOverMessage + ChatColor.GOLD + " ======")
        Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "    1st Place: " + game.positionsController?.firstPlace)
        Bukkit.getServer().broadcastMessage(ChatColor.GRAY.toString() + "    2nd Place: " + game.positionsController?.secondPlace)
        Bukkit.getServer().broadcastMessage(ChatColor.DARK_RED.toString() + "    3rd Place: " + game.positionsController?.thirdPlace)
        Bukkit.getServer().broadcastMessage(ChatColor.GOLD.toString() + "===============================")

        this.cancel()
    }

    override fun run() {
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

                game.getAllPlayers().forEach {player ->
                    //TODO: THIS NEEDS TO BE DONE EARLIER
                    player.isWhitelisted = true

                    player.showRules(game)

                    player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_GROWL, 0.2f, 1f)
                    player.health = 20.0
                    player.foodLevel = 20

                    player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

                    //TODO: THIS REFRESHES THE HEALTH SCOREBOARD TO DISPLAY CORRECTLY, IS THERE A NEATER WAY?
                    player.damage(10.0)
                    player.addPotionEffect(PotionEffect(PotionEffectType.REGENERATION, 100, 10))

                    if (game.isContestant(player)) player.isOp = false
                }

                val graceEnabled = game.configController.GRACE_END_EPISODE.get() > 0

                game.getPlayingWorlds().forEach {
                    it.difficulty = Difficulty.HARD
                    it.time = 0
                    it.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true)

                    it.pvp = !graceEnabled
                }

                onEpisodeChange(1)
            }
        }
        else {
            seconds++

            game.gamemodes.forEach { it.gameTick() }

            if (seconds == 60) {
                minutes ++
                seconds = 0

                if (minutes == episodeTime) {
                    minutes = 0
                    episodeNumber ++

                    onEpisodeChange(episodeNumber)
                }
            }

            game.configController.loadConfigFile("customize")?.let {
                var header = it.getString("in_game_tab_header")?: ""
                var footer = it.getString("in_game_tab_footer")?: ""

                header = formatTabHeaderFooter(header)
                footer = formatTabHeaderFooter(footer)

                plugin.server.onlinePlayers.forEach { it.setPlayerListHeaderFooter(header, footer) }
            }
        }
    }

}