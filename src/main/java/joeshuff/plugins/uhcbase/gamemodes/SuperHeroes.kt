package joeshuff.plugins.uhcbase.gamemodes

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class SuperHeroes(override val game: UHC): GamemodeController(game) {

    var effects = listOf(
        PotionEffect(PotionEffectType.SPEED, 1000000, 1, false, false),
        PotionEffect(PotionEffectType.INVISIBILITY, 1000000, 1, false, false),
        PotionEffect(PotionEffectType.JUMP, 1000000, 2, false, false),
        PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4, false, false),
        PotionEffect(PotionEffectType.INCREASE_DAMAGE, 1000000, 0, false, false)
    )

    override fun onGameStateChange(newState: UHC.GAME_STATE) {

    }

    override fun onEpisodeChange(episodeNumber: Int) {

    }

    override fun isEnabled(): Boolean {
        return false
    }

    override fun gameTick() {

    }

    override fun playerDeath(player: Player) {

    }

}