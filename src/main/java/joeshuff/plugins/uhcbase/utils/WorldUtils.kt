package joeshuff.plugins.uhcbase.utils

import joeshuff.plugins.uhcbase.Constants
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin

class WorldUtils {
    companion object {
        fun getHubSpawnLocation(): Location {
            return Location(
                    Bukkit.getWorld(Constants.hubWorldName),
                    Constants.hubCentreX.toDouble() + 0.5,
                    Constants.hubCentreY.toDouble() + 2,
                    Constants.hubCentreZ.toDouble() + 0.5
            )
        }

        fun JavaPlugin.getPlayingWorlds(): List<World> {
            return server.worlds.filter { it.name != Constants.hubWorldName }
        }

        fun JavaPlugin.getHubWorld(): World? {
            return server.worlds.firstOrNull { it.name == Constants.hubWorldName }
        }
    }
}