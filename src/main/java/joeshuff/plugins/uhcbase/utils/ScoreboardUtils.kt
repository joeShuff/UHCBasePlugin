package joeshuff.plugins.uhcbase.utils

import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scoreboard.DisplaySlot
import org.bukkit.scoreboard.RenderType

fun JavaPlugin.setupScoreboard(): Boolean {
    val scoreboard = server.scoreboardManager?.mainScoreboard

    if (scoreboard == null) {
        logger.severe("${ChatColor.RED}Something went wrong obtaining the scoreboard")
        return false
    }

    scoreboard.getObjective("health1")?.let { it.unregister() }
    scoreboard.getObjective("health2")?.let { it.unregister() }
    scoreboard.getObjective("kills")?.let { it.unregister() }

    scoreboard.registerNewObjective("health1", "health", "listhealth", RenderType.HEARTS).displaySlot = DisplaySlot.PLAYER_LIST
    scoreboard.registerNewObjective("health2", "health", "${ChatColor.RED}♥").displaySlot = DisplaySlot.BELOW_NAME
    scoreboard.registerNewObjective("kills", "stat.playerKills", "${ChatColor.RED}-- Kills --").displaySlot = DisplaySlot.SIDEBAR

    return true
}

fun JavaPlugin.cleanScoreboard(): Boolean {
    val scoreboard = server.scoreboardManager?.mainScoreboard

    if (scoreboard == null) {
        logger.severe("${ChatColor.RED}Something went wrong obtaining the scoreboard")
        return false
    }

    scoreboard.getObjective("health1")?.let { it.unregister() }
    scoreboard.getObjective("health2")?.let { it.unregister() }
    scoreboard.getObjective("kills")?.let { it.unregister() }

    scoreboard.teams.forEach { it.unregister() }

    return true
}