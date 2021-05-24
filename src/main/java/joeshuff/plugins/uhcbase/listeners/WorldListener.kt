package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.weather.WeatherChangeEvent
import org.bukkit.event.weather.WeatherEvent

class WorldListener(val game: UHC): Listener, Stoppable {

    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    /**
     * Cancel weather changes in the hub, if you want the hub to have a specific type of weather
     * set it in the world before you upload to the server
     */
    @EventHandler
    fun onWeatherChange(weatherEvent: WeatherChangeEvent) {
        if (weatherEvent.world.name == Constants.hubWorldName) {
            weatherEvent.isCancelled = true
        }
     }

}