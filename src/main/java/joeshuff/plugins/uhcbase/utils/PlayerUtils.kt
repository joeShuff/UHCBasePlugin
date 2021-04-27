package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.ChatColor
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

fun Player.sendDefaultTabInfo(game: UHC) {
    val config = game.configController.loadConfigFile("customize")
    config?.let {
        val header = it.getString("default_tab_header")
        val footer = it.getString("default_tab_footer")

        setPlayerListHeaderFooter(header, footer)
    }
}

/**
* This method is going to show the player that is passed as a parameter
* the game rules and game settings.
*/
fun Player.showRules(game: UHC) {
    val rules = File(game.plugin.dataFolder, "rules.yml")

    val rulesConfig: FileConfiguration = YamlConfiguration.loadConfiguration(rules)

    val rulesList = (rulesConfig.getString("rules")?: "").split(",")

    var message = "${ChatColor.RED}=======- RULES -======= _".trimIndent()

    for (rule in rulesList) {
        message = "$message${ChatColor.GOLD}• ${ChatColor.YELLOW}${rule.trim()} _".trimIndent()
    }
    message = message + ChatColor.RED + "======================= _"

    message = "$message${ChatColor.BLUE}====- GAME DATA -==== _"

    message = "$message${ChatColor.YELLOW}Apple Rates: ${ChatColor.AQUA}${game.configController.APPLE_RATE.get()}% _".trimIndent()
    message = "$message${ChatColor.YELLOW}Pearl Rates: ${ChatColor.AQUA}${game.configController.PEARL_RATE.get()}% _".trimIndent()
    message = "$message${ChatColor.YELLOW}Fall Damage: ${ChatColor.AQUA}${game.configController.FALL_DAMAGE.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}Pearl Damage: ${ChatColor.AQUA}${game.configController.PEARL_DAMAGE.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}Death Lightning: ${ChatColor.AQUA}${game.configController.DEATH_LIGHTNING.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}Nether Enabled: ${ChatColor.AQUA}${game.configController.NETHER_ENABLED.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}End Enabled: ${ChatColor.AQUA}${game.configController.END_ENABLED.get()} _".trimIndent()
    message = "$message${ChatColor.BLUE}====================="

    message.split(" _").forEach {
        sendMessage(it)
    }
}

fun Player.removeAllAdvancements() {
    server.advancementIterator().forEach {
        val advProgress: AdvancementProgress = getAdvancementProgress(it)
        advProgress.awardedCriteria.forEach {
            advProgress.revokeCriteria(it)
        }
    }
}

fun UHC.updatePlayerFlight() {
    getAllPlayers().forEach {
        it.allowFlight = isSpectator(it)
    }
}

fun UHC.updateVisibility() {
    val hideStates = listOf(UHC.GAME_STATE.PREPPED, UHC.GAME_STATE.IN_GAME)

    getAllPlayers().forEach {first ->
        getAllPlayers().filter { it.uniqueId != first.uniqueId }.forEach {second ->
            if (state !in hideStates) {
                first.showPlayer(plugin, second)
                second.showPlayer(plugin, first)
            } else {
                if (isSpectator(second)) {
                    second.canPickupItems = false
                }

                if (isContestant(first) && isSpectator(second)) {
                    first.hidePlayer(plugin, second)
                }
            }
        }
    }
}