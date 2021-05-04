package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception
import kotlin.random.Random

fun getHubSpawnLocation(): Location {
    val world = Bukkit.getWorld(Constants.hubWorldName)
    var y = Constants.hubCentreY

    if (y < 0) {
        y = world?.getHighestBlockAt(Constants.hubCentreX, Constants.hubCentreZ)?.y?: 255
    }

    return Location(
            world,
            Constants.hubCentreX.toDouble() + 0.5,
            y.toDouble() + 2,
            Constants.hubCentreZ.toDouble() + 0.5
    )
}

fun UHC.getPlayingWorlds(): List<World> {
    return plugin.server.worlds.filter { it.name != Constants.hubWorldName }
}

fun UHC.getHubWorld(): World? {
    return plugin.server.worlds.firstOrNull { it.name == Constants.hubWorldName }
}

fun String.toSeed(): Long {
    return try {
        toLong()
    } catch (ex: Exception) {
        this.hashCode().toLong()
    }
}

fun UHC.prepWorlds(worldCenter: Location, worldBorderDiameter: Int) {
    getPlayingWorlds().forEach {
        it.worldBorder.center = worldCenter
        it.worldBorder.size = worldBorderDiameter.toDouble()
        it.worldBorder.warningDistance = 25
        it.time = 0

        it.setGameRule(GameRule.NATURAL_REGENERATION, false)
        it.pvp = false
    }
}

fun UHC.prepareWorlds() {
    Bukkit.createWorld(WorldCreator(Constants.hubWorldName).environment(World.Environment.NORMAL).type(WorldType.FLAT))

    getHubWorld()?.let {
        it.pvp = false
        it.difficulty = Difficulty.PEACEFUL
    }

    val hubSeedConfig = configController.getFromConfig("UHCSeed")?.toString()?: "none"
    var seed = hubSeedConfig.toSeed()
    if (hubSeedConfig == "none") {
        seed = Random.nextLong()
    }

    Bukkit.createWorld(WorldCreator(Constants.UHCWorldName).seed(seed))
}