package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.utils.getPlayingWorlds
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class VictoryTimer(val game: UHC, val teams: Boolean): BukkitRunnable() {

    val plugin = game.plugin

    val winner = game.positionsController?.firstPlace?: ""

    val players = arrayListOf<String>()
    var seconds = 0;

    init {
        players.clear()

        if (teams) {
            val team = Bukkit.getServer().scoreboardManager?.mainScoreboard?.getTeam(winner)
            team?.let {
                it.entries.forEach {
                    plugin.server.getPlayer(it)?.let {
                        players.add(it.name)
                    }
                }
            }
        } else {
            players.add(winner)
        }

        runTaskTimer(plugin, 10, 10)
    }

    override fun run() {
        seconds++

        if (seconds % 5 == 0) {
            var cong = ChatColor.GOLD.toString() + "CONGRATULATIONS TO " + ChatColor.YELLOW
            for (p in players) {
                cong = "$cong$p "
            }
            Bukkit.getServer().broadcastMessage(cong)
        }

        if (seconds < 40) {
            for (p in players) {
                Bukkit.getPlayer(p)?.let {
                    it.world.spawnParticle(Particle.NOTE, it.location, 100, 0.5, 1.0, 0.5, 2.0)
                    playFirework(it)
                }
            }
        } else {
            game.gameState.onNext(UHC.GAME_STATE.POST_GAME)
            cancel()
        }
    }

    fun playFirework(player: Player) {
        //Spawn the Firework, get the FireworkMeta.
        val fw = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val fwm = fw.fireworkMeta

        //Our random generator
        val r = Random()

        //Get the type
        val type = listOf(
                FireworkEffect.Type.BALL,
                FireworkEffect.Type.BALL_LARGE,
                FireworkEffect.Type.BURST,
                FireworkEffect.Type.CREEPER,
                FireworkEffect.Type.STAR)
                .random()

        //Get our random colours
        val c1 = Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))
        val c2 = Color.fromRGB(r.nextInt(255), r.nextInt(255), r.nextInt(255))

        //Create our effect with this
        val effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build()

        //Then apply the effect to the meta
        fwm.addEffect(effect)

        //Generate some random power and set it
        val rp = r.nextInt(1) + 1
        fwm.power = rp

        //Then apply this to our rocket
        fw.fireworkMeta = fwm
    }
}