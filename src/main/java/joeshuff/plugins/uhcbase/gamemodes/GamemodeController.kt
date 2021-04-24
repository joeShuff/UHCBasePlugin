package joeshuff.plugins.uhcbase.gamemodes

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.entity.Player

abstract class GamemodeController(open val game: UHC) {
    val plugin = game.plugin

    abstract fun onGameStateChange(newState: UHC.GAME_STATE)

    abstract fun onEpisodeChange(episodeNumber: Int)

    abstract fun isEnabled(): Boolean

    abstract fun gameTick()

    abstract fun playerDeath(player: Player)
}