package joeshuff.plugins.uhcbase

import io.reactivex.rxjava3.subjects.BehaviorSubject
import joeshuff.plugins.uhcbase.commands.CommandController
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.gamemodes.GamemodeController
import joeshuff.plugins.uhcbase.listeners.*
import joeshuff.plugins.uhcbase.timers.GameTimer
import joeshuff.plugins.uhcbase.timers.KickTimer
import joeshuff.plugins.uhcbase.timers.PregenerationTimer
import joeshuff.plugins.uhcbase.timers.VictoryTimer
import joeshuff.plugins.uhcbase.utils.*
import org.bukkit.*
import org.bukkit.entity.Player
import java.util.*

class UHC(val plugin: UHCPlugin) {

    enum class GAME_STATE {
        PRE_GAME(),
        PREPPING(),
        PREPPED(),
        IN_GAME(),
        VICTORY_LAP(),
        POST_GAME()
    }

    val configController = ConfigController(this)

    var gameState = BehaviorSubject.createDefault(GAME_STATE.PRE_GAME)
    val state
        get() = gameState.value

    var deadList = arrayListOf<String>()
    var playingList = arrayListOf<String>()
    var spectatorList = arrayListOf<String>()

    var kickMessages: MutableMap<String, String> = mutableMapOf()

    var kickTimer: KickTimer = KickTimer(this)

    var positionsController: PositionsController? = null
    var liveGameListener: GameListener = GameListener(this)

    var ongoingPregenerationTimer: PregenerationTimer? = null

    val gamemodes = arrayListOf<GamemodeController>()

    private var teamsStored = false
    val teams: Boolean
        get() = if (state == GAME_STATE.PRE_GAME) configController.TEAMS.get() else teamsStored

    init {
        configController.initialiseConfigFiles()
        Constants.loadConstantsFromConfig(plugin)

        prepareWorlds()

        plugin.disposables.add(gameState
            .distinctUntilChanged()
            .subscribe {
                stateChange(it)
            }
        )

        updatePlayerFlight()

        //Stop the daylight cycle
        for (world in plugin.server.worlds) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }

        CommandController(this).registerCommands()

        for (player in getAllPlayers()) {
            player.sendDefaultTabInfo(this)
        }

//        gamemodes.add(FlowerPower(this))

        plugin.stoppables.let {
            it.add(BlockListener(this))
            it.add(EntityListener(this))
            it.add(PortalHandler(this))
            it.add(PlayerJoinLeaveListener(this))
            it.add(PlayerEventsListener(this))
            it.add(SpectatorEvents(this))
            it.add(WorldListener(this))
            it.add(liveGameListener)
        }
    }

    fun stateChange(state: GAME_STATE) {
        plugin.logger.info("State change to $state")

        gamemodes.forEach { it.onGameStateChange(state) }

        updateVisibility()

        when (state) {
            GAME_STATE.PRE_GAME -> {

            }
            GAME_STATE.PREPPING -> {
                teamsStored = configController.TEAMS.get()

                val world = Bukkit.getWorld(Constants.UHCWorldName) ?: run {
                    getAdmins().forEach {
                        it.sendMessage("${ChatColor.RED}Error prepping UHC. Can't find UHC world ${ChatColor.YELLOW}${Constants.UHCWorldName}")
                    }

                    return
                }

                val worldBorderDiameter = Integer.valueOf(configController.BORDER_SIZE.get())
                val worldCenter = Location(world, 0.0, (world.getHighestBlockAt(0, 0).y + 1).toDouble(), 0.0)

                prepPlayers(worldCenter)
                updatePlayerFlight()

                prepWorlds(worldCenter, worldBorderDiameter)

                plugin.setupScoreboard()

                plugin.server.broadcastMessage("${ChatColor.GREEN}World border set to $worldBorderDiameter blocks diameter.")
                plugin.server.dispatchCommand(plugin.server.consoleSender, "loc $teams")

                gameState.onNext(GAME_STATE.PREPPED)
            }
            GAME_STATE.PREPPED -> {

            }
            GAME_STATE.IN_GAME -> {
                positionsController = PositionsController(this, configController.TEAMS.get())
                GameTimer(this).runTaskTimer(plugin, 0, 20);
            }
            GAME_STATE.VICTORY_LAP -> {
                VictoryTimer(this, teams)
            }
            GAME_STATE.POST_GAME -> {
                plugin.cleanScoreboard()

                //TODO: KICK ALL PLAYERS ON COMPLETE WITH THANKS FOR PLAYING

                getAllPlayers().forEach { player ->
                    player.inventory.clear()
                    player.health = 20.0
                    player.foodLevel = 20
                    player.enderChest.clear()

                    getAllPlayers().forEach { otherPlayer ->
                        otherPlayer.showPlayer(plugin, player)
                        player.showPlayer(plugin, otherPlayer)
                    }

                    player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

                    player.teleport(getHubSpawnLocation())
                    player.gameMode = GameMode.ADVENTURE

                    player.sendDefaultTabInfo(this)
                }

                gameState.onNext(GAME_STATE.PRE_GAME)
            }
        }
    }

    fun isPlayerDead(uuid: UUID): Boolean {
        return deadList.contains(uuid.toString())
    }

    fun isPlayerDead(player: Player): Boolean {
        return isPlayerDead(player.uniqueId)
    }

    fun getAllPlayers(): List<Player> {
        return Bukkit.getOnlinePlayers().toList()
    }

    fun getContestants(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { isContestant(it) }.toList()
    }

    fun getSpectators(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { isSpectator(it) }.toList()
    }

    fun getAdmins(): List<Player> {
        return Bukkit.getOnlinePlayers().filter { it.isOp }.toList()
    }

    fun isContestant(uuid: UUID) = !isSpectator(uuid)
    fun isContestant(player: Player) = isContestant(player.uniqueId)

    fun isSpectator(uuid: UUID) = spectatorList.contains(uuid.toString()) || (!configController.OP_CONTESTANT.get() && Bukkit.getOfflinePlayer(uuid).isOp)
    fun isSpectator(player: Player) = isSpectator(player.uniqueId)
}