package joeshuff.plugins.uhcbase.config

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.items.*
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class ConfigController(val game: UHC) {

    val plugin = game.plugin

    fun getFromConfig(key: String): Any? {
        plugin.config.load(File(plugin.dataFolder, "config.yml"))
        return plugin.config.get(key)
    }

    fun getBooleanFromConfig(key: String): Boolean? = plugin.config.getBoolean(key)

    fun getDoubleFromConfig(key: String): Double? = plugin.config.getDouble(key)

    fun getIntFromConfig(key: String): Int? = plugin.config.getInt(key)

    fun setToConfig(key: String, value: Any) {
        plugin.config.set(key, value)
        plugin.config.save(File(plugin.dataFolder, "config.yml"))
    }

    val APPLE_RATE = ConfigAppleRate(this)
    val PEARL_RATE = ConfigPearlRate(this)
    val FALL_DAMAGE = ConfigFallDamage(this)
    val PEARL_DAMAGE = ConfigPearlDamage(this)
    val DEATH_LIGHTNING = ConfigDeathLightning(this)
    val ABSORBTION = ConfigAbsorbtion(this)
    val TEAMS = ConfigTeams(this)
    val FRIENDLY_FIRE = ConfigFriendlyFire(this)
    val KICK_SECONDS = ConfigKickSeconds(this)
    val CAN_SPECTATE = ConfigCanSpectate(this)
    val NETHER_ENABLED = ConfigNetherEnabled(this)
    val END_ENABLED = ConfigEndEnabled(this)
    val EPISODES_ENABLED = ConfigEpisodesEnabled(this)
    val GRACE_END_EPISODE = ConfigGraceEndEpisode(this)
    val ONE_POINT_EIGHT_PVP = ConfigOnePointEightPvp(this)
    val TELEPORT_DELAY = ConfigTeleportDelay(this)
    val TELEPORT_SIZE = ConfigTeleportSize(this)
    val PRE_GEN_TICKS = ConfigPregenTicks(this)
    val OP_CONTESTANT = ConfigOpContestant(this)
    val ANNOUNCE_FAR_ARROW = ConfigAnnounceFarArrow(this)

    val configItems = listOf(
        APPLE_RATE,
        PEARL_RATE,
        FALL_DAMAGE,
        PEARL_DAMAGE,
        DEATH_LIGHTNING,
        ABSORBTION,
        TEAMS,
        FRIENDLY_FIRE,
        KICK_SECONDS,
        CAN_SPECTATE,
        NETHER_ENABLED,
        END_ENABLED,
        EPISODES_ENABLED,
        GRACE_END_EPISODE,
        ONE_POINT_EIGHT_PVP,
        TELEPORT_DELAY,
        TELEPORT_SIZE,
        PRE_GEN_TICKS,
        OP_CONTESTANT,
        ANNOUNCE_FAR_ARROW
    )

    val filesToCreate = listOf("rules.yml", "customize.yml", "modes.yml")

    fun initialiseConfigFiles() {
        plugin.saveDefaultConfig()

        filesToCreate.forEach {fileName ->
            val configFile = File(plugin.dataFolder, fileName)
            if (!configFile.exists()) plugin.saveResource(fileName, false)
        }
    }

    fun getConfigItem(key: String): ConfigItem<out Any>? {
        return this.configItems.firstOrNull { it.configKey == key } ?: null
    }

    fun loadConfigFile(filename: String): FileConfiguration? {
        try {
            val config = YamlConfiguration()
            config.load(File(plugin.dataFolder, "$filename.yml"))
            return config
        } catch (e: Exception) {
            return null
        }
    }
}