package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.timers.KickTimer
import joeshuff.plugins.uhcbase.utils.getHubSpawnLocation
import org.bukkit.*
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerTeleportEvent
import org.bukkit.event.world.PortalCreateEvent
import org.bukkit.potion.PotionEffectType
import java.text.DecimalFormat
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class GameListener(val game: UHC): Listener, Stoppable {

    val plugin = game.plugin

    var arrowSource: MutableMap<Int, Location> = mutableMapOf()

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun logOut(event: PlayerQuitEvent) {
        if (game.state == UHC.GAME_STATE.IN_GAME && !game.isPlayerDead(event.player)) {
            game.kickTimer?.loggedOutList?.add(
                KickTimer.PlayerLogOut(
                    event.player,
                    (System.currentTimeMillis() / 1000).toInt()
                )
            )
        }
    }

    @EventHandler
    fun logIn(event: PlayerLoginEvent) {
        if (!event.player.isWhitelisted && Bukkit.getServer().hasWhitelist()) {
            var notWhitelistedMessage = "You are not whitelisted on this server."

            plugin.getConfigController().loadConfigFile("customize")?.let {
                it.getString("not_whitelisted_message")?.let {
                    notWhitelistedMessage = it
                }
            }

            game.kickMessages[event.player.name]?.let { kickMessage ->
                var kickMessageTemplate = kickMessage

                plugin.getConfigController().loadConfigFile("customize")?.let {
                    kickMessageTemplate = (it.getString("kick_message")?: "{kickmessage}")
                            .replace("{kickmessage}", kickMessage)
                            .replace("{playername}", event.player.displayName)
                            .replace("\\n", "\n")
                }

                event.disallow(PlayerLoginEvent.Result.KICK_OTHER, kickMessageTemplate)
                return
            }?: run {
                event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, notWhitelistedMessage)
                return
            }
        }

        if (game.isPlayerDead(event.player)) {
            event.player.canPickupItems = false
            plugin.server.scheduler.scheduleSyncDelayedTask(plugin, {
                event.player.gameMode = GameMode.SPECTATOR
                event.player.inventory.clear()
            }, 5)
        }

        if (game.state == UHC.GAME_STATE.IN_GAME) {
            game.kickTimer.loggedOutList.removeIf { it.player.name == event.player.name }
        }
    }

    @EventHandler
    fun playerDeath(event: PlayerDeathEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) {
            return
        }

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
        game.kickMessages[player.name] = kickMessage
        Bukkit.broadcastMessage(deathMessage)

        val spawnLoc = player.location

        if (spawnLoc.y < 0) {
            spawnLoc.y = 20.0
        }

        game.gamemodes.forEach { it.playerDeath(player) }

        player.setBedSpawnLocation(spawnLoc, true)

        player.gameMode = GameMode.SPECTATOR

        if (plugin.getConfigController().DEATH_LIGHTNING.get()) {
            player.world.strikeLightningEffect(player.location)
        }

        if (!plugin.getConfigController().CAN_SPECTATE.get()) {
            if (plugin.server.getWorld(Constants.hubWorldName) != null) {
                player.gameMode = GameMode.ADVENTURE
                player.setBedSpawnLocation(getHubSpawnLocation(), true)

                if (player.bedSpawnLocation == null) {
                    player.gameMode = GameMode.SPECTATOR
                }
            }
        }

        var killer = "Entity"

        player.killer?.name?: run {
            val objective = player.server.scoreboardManager?.mainScoreboard?.getObjective("uhckills")
            objective?.let {
                val score = it.getScore("" + ChatColor.AQUA + ChatColor.BOLD + "PvE:")
                val newscore = score.score + 1
                score.score = newscore
            }
        }

        Bukkit.getOnlinePlayers().filter { it.uniqueId != player.uniqueId }.forEach {
            if (game.isPlayerDead(it)) {
                player.showPlayer(plugin, it)
            } else {
                it.hidePlayer(plugin, player)
            }
        }

        if (plugin.getConfigController().KICK_SECONDS.get() > 0) {
            if (!offlineKill) {
                val nowSeconds = (System.currentTimeMillis() / 1000).toInt()
                val secondsTilKick = plugin.getConfigController().KICK_SECONDS.get()
                game.kickTimer.kickUsersAt.add(KickTimer.PlayerKick(player, nowSeconds + secondsTilKick))
            }

            player.isWhitelisted = false
        }

        game.deadList.add(player.uniqueId.toString())
        game.positionsController?.onPlayerDeath(player)
    }

    @EventHandler
    fun tpEvent(event: PlayerTeleportEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) return

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

        if (game.state != UHC.GAME_STATE.IN_GAME) {
            event.isCancelled = true
            return
        }

        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            if (!plugin.getConfigController().FALL_DAMAGE.get()) {
                event.isCancelled = true
                return
            }
        }

//        DataTracker.playerInfo[event.entity.name]?.tookDamage(event.finalDamage)
    }

    @EventHandler
    fun arrowShot(event: EntitySpawnEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) return

        if (event.entity is Arrow) {
            arrowSource[event.entity.entityId] = event.location
        }
    }

    @EventHandler
    fun arrowLand(event: ProjectileHitEvent) {

        if (game.state != UHC.GAME_STATE.IN_GAME) return

        if (event.entity is Arrow) {
            val exec = Executors.newScheduledThreadPool(1)
            exec.schedule({ arrowSource.remove(event.entity.entityId) }, 1, TimeUnit.SECONDS)
        }
    }

    @EventHandler
    fun entityDamagedByEntity(event: EntityDamageByEntityEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) {
            event.isCancelled = true
            return
        }

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
    fun potionEffectEvent(event: EntityPotionEffectEvent) {
        if (event.action == EntityPotionEffectEvent.Action.REMOVED || event.action == EntityPotionEffectEvent.Action.CLEARED) {
            return
        }

        if (event.modifiedType == PotionEffectType.ABSORPTION && game.state == UHC.GAME_STATE.IN_GAME) {
            if (plugin.getConfigController().ABSORBTION.get()) {
                return
            }

            if (event.entity is Player) {
                event.isCancelled = true
            }
        }
    }
}