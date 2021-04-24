package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.config.getConfigController
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

fun JavaPlugin.getPlayingWorlds(): List<World> {
    return server.worlds.filter { it.name != Constants.hubWorldName }
}

fun JavaPlugin.getHubWorld(): World? {
    return server.worlds.firstOrNull { it.name == Constants.hubWorldName }
}

fun String.toSeed(): Long {
    return try {
        toLong()
    } catch (ex: Exception) {
        this.hashCode().toLong()
    }
}

fun JavaPlugin.prepareWorlds() {
    Bukkit.createWorld(WorldCreator(Constants.hubWorldName).environment(World.Environment.NORMAL).type(WorldType.FLAT))

    getHubWorld()?.let {
        it.pvp = false
        it.difficulty = Difficulty.PEACEFUL
    }

    val hubSeedConfig = getConfigController().UHC_WORLD_SEED.get()
    var seed = hubSeedConfig.toSeed()
    if (hubSeedConfig == "none") {
        seed = Random.nextLong()
    }

    Bukkit.createWorld(WorldCreator(Constants.UHCWorldName).seed(seed))
}