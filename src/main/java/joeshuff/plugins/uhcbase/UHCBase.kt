package joeshuff.plugins.uhcbase

import joeshuff.plugins.uhcbase.Constants.Companion.loadConstantsFromConfig
import joeshuff.plugins.uhcbase.commands.CommandController
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.gamemodes.FlowerPower
import joeshuff.plugins.uhcbase.gamemodes.GamemodeController
import joeshuff.plugins.uhcbase.listeners.BlockListener
import joeshuff.plugins.uhcbase.listeners.EntityListener
import joeshuff.plugins.uhcbase.listeners.GameListener
import joeshuff.plugins.uhcbase.listeners.PortalHandler
import joeshuff.plugins.uhcbase.permissions.Permissions.Companion.initialisePermissions
import joeshuff.plugins.uhcbase.timers.PregenerationTimer
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.getHubWorld
import joeshuff.plugins.uhcbase.utils.WorldUtils.Companion.toSeed
import joeshuff.plugins.uhcbase.utils.sendDefaultTabInfo
import org.bukkit.*
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class UHCBase: JavaPlugin() {

    var UHCPrepped = false
    var UHCLive = false
    var UHCVictoryLap = false

    var positionsController: PositionsController? = null
    var liveGameListener: GameListener? = null

    var ongoingPregenerationTimer: PregenerationTimer? = null

    val gamemodes = arrayListOf<GamemodeController>()

    override fun onEnable() {
        ConfigController(this).initialiseConfigFiles()
        initialisePermissions(this)
        loadConstantsFromConfig(this)

        Bukkit.createWorld(WorldCreator(Constants.hubWorldName).environment(World.Environment.NORMAL).type(WorldType.FLAT))

        val hubSeedConfig = getConfigController().UHC_WORLD_SEED.get()
        var seed = hubSeedConfig.toSeed()
        if (hubSeedConfig == "none") {
            seed = Random.nextLong()
        }
        Bukkit.createWorld(WorldCreator(Constants.UHCWorldName).seed(seed))

        gamemodes.add(FlowerPower(this))

        //Stop the daylight cycle
        for (world in server.worlds) {
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        }

        getHubWorld()?.let {
            it.pvp = false
            it.difficulty = Difficulty.PEACEFUL
        }

        BlockListener(this)
        EntityListener(this)
        PortalHandler(this)
        CommandController(this).registerCommands()

        for (player in Bukkit.getServer().onlinePlayers) {
            player.sendDefaultTabInfo(this)
        }

        logger.info("==============================")
        logger.info("Joe Shuff's UHC Plugin Enabled")
        logger.info("==============================")
    }

    override fun onDisable() {}

//    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
//        if (command.name == "nominate" && sender is Player) {
//            if (UHCLive) {
//                sender.sendMessage(ChatColor.RED.toString() + "Not able to use command")
//                return true
//            }
//            if (args.size == 1) {
//                Season14.nominate(sender as Player, args[0])
//            } else {
//                sender.sendMessage(ChatColor.RED.toString() + "Insufficient Arguments - /nominate <player>")
//            }
//            return true
//        }
//        return false
//    }
}