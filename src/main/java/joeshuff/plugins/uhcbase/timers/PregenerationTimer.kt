package joeshuff.plugins.uhcbase.timers

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHC
import org.bukkit.ChatColor
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable
import java.lang.Exception
import kotlin.math.*

class PregenerationTimer(val game: UHC, val diameter: Int): BukkitRunnable() {

    val plugin = game.plugin

    var world: World? = null
    val maxChunkDist: Int = ceil(ceil(diameter / 16.0) / 2.0).toInt()

    var totalChunksToGen = (maxChunkDist * 2).toDouble().pow(2.0)
    var chunksGenerated = 0

    var chunksToGenerate = arrayListOf<ChunkCoord>()
    var chunksToUnload = arrayListOf<ChunkCoord>()

    var initialised = false

    var abortGeneration = false

    val pregenDelay = game.configController.PRE_GEN_TICKS.get()

    class ChunkCoord(val x: Int, val z: Int)

    init {
        runTaskTimer(plugin, 0, pregenDelay.toLong())
    }

    private fun setup(): Boolean {
        world = plugin.server.getWorld(Constants.UHCWorldName)

        chunksToGenerate.clear()
        chunksGenerated = 0
        chunksToUnload.clear()

        plugin.server.broadcastMessage("${ChatColor.YELLOW}Analysing chunks...")

        (-maxChunkDist .. maxChunkDist).forEach {x ->
            (-maxChunkDist .. maxChunkDist).forEach { z ->
                if (world?.isChunkGenerated(x, z) == false) {
                    chunksToGenerate.add(ChunkCoord(x, z))
                }
            }
        }

        totalChunksToGen = chunksToGenerate.size.toDouble()

        val ticksToGen = ceil(totalChunksToGen / 4).toInt()
        val secondsToGen = (ticksToGen * pregenDelay) / 20

        val minutes = floor(secondsToGen / 60.0).toInt()
        val seconds = secondsToGen % 60

        if (chunksToGenerate.isEmpty()) {
            plugin.server.broadcastMessage("${ChatColor.GOLD}All chunks are already generated...")
            stop(false)
            return false
        } else {
            plugin.server.broadcastMessage("${ChatColor.GOLD}Generating ${totalChunksToGen.toInt()} chunks. estimated time: ${minutes}m${seconds}s")
        }

        return true
    }

    fun stop(abort: Boolean = true) {
        chunksToUnload.forEach {
            world?.unloadChunk(it.x, it.z, true)
        }

        world?.save()

        if (abort) {
            val perc = (chunksGenerated.toFloat() / totalChunksToGen.toFloat()).times(100).roundToInt()
            plugin.server.broadcastMessage("${ChatColor.RED}WORLD PREGENERATION ABORTED ($perc% COMPLETED)")
        }

        game.ongoingPregenerationTimer = null

        try { super.cancel() } catch (e: Exception) {}
    }

    override fun run() {
        if (!initialised) {
            initialised = true
            if (!setup()) return
        }

        if (abortGeneration) {
            stop(true)
            return
        }

        world?.let {world ->
            val perc = (chunksGenerated.toFloat() / totalChunksToGen.toFloat()).times(100).roundToInt()
            plugin.server.broadcastMessage("${ChatColor.GREEN}Generating chunks... $chunksGenerated/$totalChunksToGen ($perc%)")

            chunksToUnload.forEach {
                world.unloadChunk(it.x, it.z, true)
            }

            chunksToUnload.clear()

            repeat(4) {
                if (chunksToGenerate.isEmpty()) {
                    plugin.server.broadcastMessage("${ChatColor.GREEN}WORLD PREGENERATION COMPLETED")
                    stop(false)
                    return
                }

                val thischunk = chunksToGenerate.removeAt(0)
                val loaded = world.loadChunk(thischunk.x, thischunk.z, true)

                if (!loaded) {
                    plugin.logger.info("Couldn't load ${thischunk.x} ${thischunk.z}")
                }

                chunksToUnload.add(thischunk)
                chunksGenerated++
            }
        }
    }
}