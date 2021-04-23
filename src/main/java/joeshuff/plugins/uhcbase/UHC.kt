package joeshuff.plugins.uhcbase

import io.reactivex.rxjava3.subjects.BehaviorSubject
import joeshuff.plugins.uhcbase.commands.CommandController
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.gamemodes.FlowerPower
import joeshuff.plugins.uhcbase.gamemodes.GamemodeController
import joeshuff.plugins.uhcbase.listeners.*
import joeshuff.plugins.uhcbase.timers.GameTimer
import joeshuff.plugins.uhcbase.timers.KickTimer
import joeshuff.plugins.uhcbase.timers.PregenerationTimer
import joeshuff.plugins.uhcbase.timers.VictoryTimer
import joeshuff.plugins.uhcbase.utils.cleanScoreboard
import joeshuff.plugins.uhcbase.utils.getHubSpawnLocation
import joeshuff.plugins.uhcbase.utils.prepareWorlds
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.entity.Player
import java.util.*

class UHC(val plugin: UHCPlugin) {

    enum class GAME_STATE {
        PRE_GAME(),
        PREPPED(),
        IN_GAME(),
        VICTORY_LAP(),
        POST_GAME()
    }

    var gameState = BehaviorSubject.createDefault(GAME_STATE.PRE_GAME)
    val state
        get() = gameState.value

    var deadList: ArrayList<String> = ArrayList()
    var playingList: ArrayList<String> = ArrayList()

    var kickMessages: MutableMap<String, String> = mutableMapOf()

    var kickTimer: KickTimer = KickTimer(this)

    var positionsController: PositionsController? = null
    var liveGameListener: GameListener = GameListener(this)

    var ongoingPregenerationTimer: PregenerationTimer? = null

    val gamemodes = arrayListOf<GamemodeController>()

    val teams: Boolean = plugin.getConfigController().TEAMS.get()

    init {
        ConfigController(plugin).initialiseConfigFiles()
        Constants.loadConstantsFromConfig(plugin)

        plugin.prepareWorlds()

        gameState
            .distinctUntilChanged()
            .subscribe {
                stateChange(it)
            }

        //Stop the daylight cycle
        for (world in plugin.server.worlds) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }

        CommandController(this).registerCommands()

        for (player in Bukkit.getServer().onlinePlayers) {
            player.sendDefaultTabInfo(plugin)
        }

//        gamemodes.add(FlowerPower(this))

        plugin.stoppables.let {
            it.add(BlockListener(this))
            it.add(EntityListener(this))
            it.add(PortalHandler(this))
            it.add(PlayerJoinLeaveListener(this))
            it.add(liveGameListener)
        }
    }

    fun stateChange(state: GAME_STATE) {
        plugin.logger.info("State change to $state")

        gamemodes.forEach { it.onGameStateChange(state) }

        when (state) {
            GAME_STATE.PRE_GAME -> {

            }
            GAME_STATE.PREPPED -> {

            }
            GAME_STATE.IN_GAME -> {
                positionsController = PositionsController(this, plugin.getConfigController().TEAMS.get())
                GameTimer(this).runTaskTimer(plugin, 0, 20);
            }
            GAME_STATE.VICTORY_LAP -> {
                VictoryTimer(this, teams)
            }
            GAME_STATE.POST_GAME -> {
                plugin.cleanScoreboard()

                //TODO: KICK ALL PLAYERS ON COMPLETE WITH THANKS FOR PLAYING

                plugin.server.onlinePlayers.forEach { player ->
                    player.inventory.clear()
                    player.health = 20.0
                    player.foodLevel = 20
                    player.enderChest.clear()

                    plugin.server.onlinePlayers.forEach { otherPlayer ->
                        otherPlayer.showPlayer(plugin, player)
                        player.showPlayer(plugin, otherPlayer)
                    }

                    player.activePotionEffects.forEach { player.removePotionEffect(it.type) }

                    player.teleport(getHubSpawnLocation())
                    player.gameMode = GameMode.ADVENTURE

                    player.sendDefaultTabInfo(plugin)
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

    fun isContestant(uuid: UUID): Boolean {
        return playingList.contains(uuid.toString())
    }

    fun isContestant(player: Player): Boolean {
        return isContestant(player.uniqueId)
    }
}