package joeshuff.plugins.uhcbase

import joeshuff.plugins.uhcbase.listeners.Stoppable
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class UHCPlugin: JavaPlugin() {

    val stoppables = arrayListOf<Stoppable>()

    override fun onEnable() {
        try {
            UHC(this)

            logger.info("==============================")
            logger.info("Joe Shuff's UHC Plugin Enabled")
            logger.info("==============================")
        } catch (e: Exception) {
            e.printStackTrace()
            logger.severe("Something went wrong starting UHC Plugin: ${e.message}")
            Bukkit.getPluginManager().disablePlugin(this)
        }
    }

    override fun onDisable() {
        stoppables.forEach { it.stop() }
    }
}