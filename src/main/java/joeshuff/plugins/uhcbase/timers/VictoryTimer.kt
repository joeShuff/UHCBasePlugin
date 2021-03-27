package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.Constants.Companion.hubCentreX
import joeshuff.plugins.uhcbase.Constants.Companion.hubCentreY
import joeshuff.plugins.uhcbase.Constants.Companion.hubCentreZ
import joeshuff.plugins.uhcbase.Constants.Companion.hubWorldName
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import org.bukkit.*
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class VictoryTimer(val plugin: UHCBase, val teams: Boolean, val winner: String): BukkitRunnable() {

    val players = arrayListOf<String>()
    var seconds = 0;

    init {
        players.clear()

        plugin.getPlayingWorlds().forEach {
            it.pvp = false
        }

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
                var player = Bukkit.getServer().getPlayer(p)
                for (p1 in Bukkit.getOnlinePlayers()) {
                    if (p1.name == p) {
                        player = p1
                    }
                }
                cong = "$cong$p "
            }
            Bukkit.getServer().broadcastMessage(cong)
        }

        if (seconds < 40) {
            for (p in players) {
                var player = Bukkit.getServer().getPlayer(p)
                for (p1 in Bukkit.getOnlinePlayers()) {
                    if (p1.name == p) {
                        player = p1
                    }
                }
                if (player != null) {
                    player.spawnParticle(Particle.NOTE, player.location, 100, 0.5, 1.0, 0.5, 2.0)
                    playFirework(player, player.location)
                }
            }
        } else {
            for (player in Bukkit.getOnlinePlayers()) {
                val world = Bukkit.getWorld(hubWorldName)
                var loc: Location
                if (world != null) {
                    loc = Location(world, hubCentreX.toDouble(), hubCentreY.toDouble(), hubCentreZ.toDouble())
                    player.teleport(loc)
                    player.inventory.clear()
                    player.inventory.boots = null
                    player.inventory.leggings = null
                    player.inventory.helmet = null
                    player.inventory.chestplate = null
                    player.gameMode = GameMode.SURVIVAL
                }
            }
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop-uhc")
            cancel()
        }
    }

    fun playFirework(player: Player, loc: Location?) {
        //Spawn the Firework, get the FireworkMeta.
        val fw = player.world.spawnEntity(player.location, EntityType.FIREWORK) as Firework
        val fwm = fw.fireworkMeta

        //Our random generator
        val r = Random()

        //Get the type
        val rt = r.nextInt(5) + 1
        var type = FireworkEffect.Type.BALL
        if (rt == 1) type = FireworkEffect.Type.BALL
        if (rt == 2) type = FireworkEffect.Type.BALL_LARGE
        if (rt == 3) type = FireworkEffect.Type.BURST
        if (rt == 4) type = FireworkEffect.Type.CREEPER
        if (rt == 5) type = FireworkEffect.Type.STAR

        //Get our random colours
        val r1i = r.nextInt(17) + 1
        val r2i = r.nextInt(17) + 1
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