package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.datatracker.DataTracker
import joeshuff.plugins.uhcbase.timers.KickTimer
import joeshuff.plugins.uhcbase.utils.WorldUtils
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getPlayingWorlds
import org.bukkit.*
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEnderPearl
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.*
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.potion.PotionEffectType
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class PlayerListener(val plugin: UHCBase): Listener {

    var deadList: ArrayList<String> = ArrayList()
    var playingList: ArrayList<String> = ArrayList()

    var kickMessages: MutableMap<String, String> = mutableMapOf()

    var arrowSource: MutableMap<Int, Location> = mutableMapOf()

    var kickTimer: KickTimer? = null

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
        kickTimer = KickTimer(this)
    }

    fun stop() {
        HandlerList.unregisterAll(this)
        kickTimer?.stop()
    }

    @EventHandler
    fun logOut(event: PlayerQuitEvent) {
        if (plugin.UHCLive && !deadList.contains(event.player.name)) {
            kickTimer?.loggedOutList?.add(KickTimer.PlayerLogOut(event.player, (System.currentTimeMillis() / 1000).toInt()))
        }
    }

    @EventHandler
    fun logIn(event: PlayerLoginEvent) {
        if (!event.player.isWhitelisted && Bukkit.getServer().hasWhitelist()) {
            var notWhitelistedMessage = "You are not whitelisted on this server."

            plugin.getConfigController().loadConfigFile("customize")?.let {
                notWhitelistedMessage = it.getString("not_whitelisted_message")?: "You are not whitelisted on this server."
            }

            kickMessages[event.player.name]?.let {kickMessage ->
                var kickMessageTemplate = kickMessage

                plugin.getConfigController().loadConfigFile("customize")?.let {
                    kickMessageTemplate = (it.getString("kick_message")?: "{kickmessage}")
                            .replace("{kickmessage}", kickMessage)
                            .replace("{playername}", event.player.displayName)
                            .replace("\\n", "\n")
                }

                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, kickMessageTemplate)
                return
            }?: run {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, notWhitelistedMessage)
                return
            }
        }

        if (deadList.contains(event.player.name)) {
            event.player.canPickupItems = false
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                event.player.gameMode = GameMode.SPECTATOR
                event.player.inventory.clear()
            }, 5)
        }

        if (plugin.UHCLive) {
            kickTimer?.loggedOutList?.removeIf { it.player.name == event.player.name }
        }
    }

    @EventHandler
    fun playerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        var kickReason = ""
        var deathMessage = ""

        event.deathMessage?.let {
            deathMessage = it
            kickReason = ChatColor.RED.toString() + it.replace(player.name, "You")?.replace("was", "were")
        }

        event.deathMessage = null
        handlePlayerDeath(player, deathMessage, kickReason)
    }

    @EventHandler
    fun portalLight(event: PortalCreateEvent) {
        if (event.world.environment == World.Environment.NORMAL) {
            if (event.reason == PortalCreateEvent.CreateReason.FIRE) {
                if (!plugin.getConfigController().NETHER_ENABLED.get()) {
                    event.isCancelled = true
                }
            }
        }
    }

    fun handlePlayerDeath(player: Player, deathMessage: String = "", kickMessage: String = "", offlineKill: Boolean = false) {
        kickMessages[player.name] = kickMessage
        Bukkit.broadcastMessage(deathMessage)

        val spawnLoc = player.location

        if (spawnLoc.y < 0) {
            spawnLoc.y = 20.0
        }

//        Season14.death(player)

        player.setBedSpawnLocation(spawnLoc, true)

        player.gameMode = GameMode.SPECTATOR

        if (plugin.getConfigController().DEATH_LIGHTNING.get()) {
            player.world.strikeLightningEffect(player.location)
        }

        if (!plugin.getConfigController().CAN_SPECTATE.get()) {
            if (plugin.server.getWorld(Constants.hubWorldName) != null) {
                player.gameMode = GameMode.ADVENTURE
                player.setBedSpawnLocation(WorldUtils.getHubSpawnLocation(), true)

                if (player.bedSpawnLocation == null) {
                    player.gameMode = GameMode.SPECTATOR
                }
            }
        }

        var killer = "Entity"

        player.killer?.name?.let {

        }?: run {
            val objective = player.server.scoreboardManager?.mainScoreboard?.getObjective("uhckills")
            objective?.let {
                val score = it.getScore("" + ChatColor.AQUA + ChatColor.BOLD + "PvE:")
                val newscore = score.score + 1
                score.score = newscore
            }
        }

        Bukkit.getOnlinePlayers().forEach {
            if (deadList.contains(it.name)) {
                player.showPlayer(it)
            } else {
                it.hidePlayer(player)
            }
        }

        if (plugin.getConfigController().KICK_ON_DEATH.get()) {
            if (!offlineKill) {
                val nowSeconds = (System.currentTimeMillis() / 1000).toInt()
                val secondsTilKick = plugin.getConfigController().KICK_SECONDS.get()
                kickTimer?.kickUsersAt?.add(KickTimer.PlayerKick(player, nowSeconds + secondsTilKick))
            }

            player.isWhitelisted = false
        }

        deadList.add(player.name)
        plugin.positionsController?.onPlayerDeath(player)
    }

    @EventHandler
    fun changeWorld(event: PlayerChangedWorldEvent) {
        plugin.getPlayingWorlds().forEach {
            it.setGameRule(GameRule.NATURAL_REGENERATION, false)
            it.difficulty = Difficulty.HARD
        }
    }

    @EventHandler
    fun tpEvent(event: PlayerTeleportEvent) {
        if (event.cause == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            if (!plugin.getConfigController().PEARL_DAMAGE.get()) {
                event.isCancelled = true
                event.to?.let { event.player.teleport(it) }
            }
        }
    }

    @EventHandler
    fun damage(event: EntityDamageEvent) {
        if (event.entity !is Player) return

        if (event is EntityDamageByEntityEvent) return

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            if (!plugin.getConfigController().FALL_DAMAGE.get()) {
                event.isCancelled = true
                return
            }
        }

//        DataTracker.playerInfo[event.entity.name]?.tookDamage(event.finalDamage)
    }

    @EventHandler
    fun eat(event: PlayerItemConsumeEvent) {
        if (event.item.type == Material.GOLDEN_APPLE) {
//            DataTracker.playerInfo[event.player.name]?.gappleConsumed()
        }
    }

    @EventHandler
    fun arrowShot(event: EntitySpawnEvent) {
        if (event.entity is Arrow) {
            arrowSource[event.entity.entityId] = event.location
        }
    }

    @EventHandler
    fun arrowLand(event: ProjectileHitEvent) {
        if (event.entity is Arrow) {
            val exec = Executors.newScheduledThreadPool(1)
            exec.schedule({ arrowSource.remove(event.entity.entityId) }, 1, TimeUnit.SECONDS)
        }
    }

    @EventHandler
    fun entityDamagedByEntity(event: EntityDamageByEntityEvent) {
        if (event.damager is Arrow && event.entity is Player) {
            val damaged = event.entity as Player
            val arrow = event.damager as Arrow

            if (arrow.shooter is Player) {
                val shooter = arrow.shooter as Player
                var distance = -1.0
                var distanceText = ""

                arrowSource[arrow.entityId]?.let {
                    distance = event.entity.location.distance(it)

                    val format = DecimalFormat("####0.00")
                    distanceText = format.format(distance)
                }

                if (distance > 25) {
                    Bukkit.broadcastMessage("${ChatColor.RED}${shooter.displayName} shot ${damaged.displayName} from ${ChatColor.YELLOW}$distanceText ${ChatColor.RED}blocks away!")
                } else {
                    shooter.sendMessage("${ChatColor.RED}You shot ${damaged.displayName} from ${ChatColor.YELLOW}$distanceText ${ChatColor.RED}blocks away!")
                }
            }

            arrowSource.remove(arrow.entityId)
        }
    }

    @EventHandler
    fun playerHeal(event: EntityRegainHealthEvent) {
        if (plugin.getConfigController().ABSORBTION.get()) {
            return
        }

        if (event.entity is Player) {
            (event.entity as Player).removePotionEffect(PotionEffectType.ABSORPTION)
        }
    }
}