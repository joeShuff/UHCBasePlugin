package joeshuff.plugins.uhcbase.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.lang.Exception

fun JavaPlugin.getConfigController() = ConfigController(this)

class ConfigController(val plugin: JavaPlugin) {

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

    //====GAME DATA=====
    var APPLE_RATE = ConfigItem<Double>(this, "apple-rate", 0.5, minDouble = 0.0, maxDouble = 100.0)

    var PEARL_RATE = ConfigItem<Double>(this, "pearl-rate", 0.5, minDouble = 0.0, maxDouble = 100.0)

    var FALL_DAMAGE = ConfigItem(this, "fall-damage", true)

    var PEARL_DAMAGE = ConfigItem(this, "pearl-damage", true)

    var DEATH_LIGHTNING = ConfigItem(this, "death-lightning", true)

    var ABSORBTION = ConfigItem(this, "absorbtion", true)

    var TEAMS = ConfigItem(this, "teams", true)

    var FRIENDLY_FIRE = ConfigItem(this, "friendly-fire", true)

    var KICK_SECONDS = ConfigItem(this, "seconds-until-kick", 30, minInt = 0)

    var CAN_SPECTATE = ConfigItem(this, "can-spectate", true)

    var NETHER_ENABLED = ConfigItem(this, "nether-enabled", true)

    var END_ENABLED = ConfigItem(this, "end-enabled", false)

    var EPISODES_ENABLED = ConfigItem(this, "show-episodes", true)

    var GRACE_END_EPISODE = ConfigItem(this, "grace-end-episode", 2, minInt = 0)
    //=================

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
            GRACE_END_EPISODE
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