package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.ChatColor
import org.bukkit.advancement.AdvancementProgress
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

fun Player.sendDefaultTabInfo(plugin: JavaPlugin) {
    val config = plugin.getConfigController().loadConfigFile("customize")
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
fun Player.showRules(plugin: JavaPlugin) {
    val rules = File(plugin.dataFolder, "rules.yml")

    val rulesConfig: FileConfiguration = YamlConfiguration.loadConfiguration(rules)

    val rulesList = (rulesConfig.getString("rules")?: "").split(",")

    var message = "${ChatColor.RED} =======- RULES -======= _".trimIndent()

    for (rule in rulesList) {
        message = "$message${ChatColor.GOLD}• ${ChatColor.YELLOW}${rule.trim()} _".trimIndent()
    }
    message = message + ChatColor.RED + "======================= _"

    message = "$message${ChatColor.BLUE}====- GAME DATA -==== _"

    message = "$message${ChatColor.YELLOW}Apple Rates: ${ChatColor.AQUA}${plugin.getConfigController().APPLE_RATE.get()}% _".trimIndent()
    message = "$message${ChatColor.YELLOW}Pearl Rates: ${ChatColor.AQUA}${plugin.getConfigController().PEARL_RATE.get()}% _".trimIndent()
    message = "$message${ChatColor.YELLOW}Fall Damage: ${ChatColor.AQUA}${plugin.getConfigController().FALL_DAMAGE.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}Pearl Damage: ${ChatColor.AQUA}${plugin.getConfigController().PEARL_DAMAGE.get()} _".trimIndent()
    message = "$message${ChatColor.YELLOW}Death Lightning: ${ChatColor.AQUA}${plugin.getConfigController().DEATH_LIGHTNING.get()} _".trimIndent()
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