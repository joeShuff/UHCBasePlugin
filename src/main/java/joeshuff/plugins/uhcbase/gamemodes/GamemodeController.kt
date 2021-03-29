package joeshuff.plugins.uhcbase.gamemodes

import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

interface GamemodeController {

    fun onGameStart() {}

    fun onGameEnd() {}

    fun onEpisodeChange(episodeNumber: Int) {}

    fun isEnabled(): Boolean

    fun gameTick() {}

    fun playerDeath(player: Player) {}
}