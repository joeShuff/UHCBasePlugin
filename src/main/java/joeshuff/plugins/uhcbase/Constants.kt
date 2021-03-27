package joeshuff.plugins.uhcbase

import org.bukkit.plugin.java.JavaPlugin

class Constants {
    companion object {
        var UHCWorldName = ""

        var hubWorldName = ""

        var hubCentreX = 0
        var hubCentreY = 255
        var hubCentreZ = 0

        fun loadConstantsFromConfig(plugin: JavaPlugin) {
            UHCWorldName = plugin.config.getString("UHCWorld")?: ""

            if (UHCWorldName.isEmpty()) {
                plugin.logger.info("Can't find a world name for UHCWorld in the config")
            }

            hubWorldName = plugin.config.getString("hubWorld")?: ""

            if (hubWorldName.isEmpty()) {
                plugin.logger.info("Can't find a world name for hubWorld in the config")
            }

            try {
                hubCentreX = plugin.config.getInt("hubCentreX")
                hubCentreY = plugin.config.getInt("hubCentreY")
                hubCentreZ = plugin.config.getInt("hubCentreZ")
            } catch (e: Exception) {
                plugin.logger.info("Can't find the hub centre from the config!")
            }
        }
    }
}

