package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.Constants
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.lang.Exception

class WorldUtils {
    companion object {
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
    }
}