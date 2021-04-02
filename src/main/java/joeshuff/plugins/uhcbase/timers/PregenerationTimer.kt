package joeshuff.plugins.uhcbase.timers

import com.mojang.datafixers.kinds.Const
import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.UHCBase
import org.bukkit.ChatColor
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.*

class PregenerationTimer(val plugin: UHCBase, val diameter: Int): BukkitRunnable() {

    var world: World? = null
    val maxChunkDist: Int = ceil(ceil(diameter / 16.0) / 2.0).toInt()

    var totalChunksToGen = (maxChunkDist * 2).toDouble().pow(2.0)
    var chunksGenerated = 0

    var chunksToGenerate = arrayListOf<ChunkCoord>()
    var chunksToUnload = arrayListOf<ChunkCoord>()

    class ChunkCoord(val x: Int, val z: Int)

    init {
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

        val timeToGen = ceil(totalChunksToGen / 4).toInt()

        val minutes = floor(timeToGen / 60.0).toInt()
        val seconds = timeToGen % 60

        if (chunksToGenerate.isEmpty()) {
            plugin.server.broadcastMessage("${ChatColor.GOLD}All chunks are already generated...")
        } else {
            runTaskTimer(plugin, 0, 20)
            plugin.server.broadcastMessage("${ChatColor.GOLD}Generating ${totalChunksToGen.toInt()} chunks. estimated time: ${minutes}m${seconds}s")
        }
    }

    override fun run() {
        world?.let {world ->
            val perc = (chunksGenerated.toFloat() / totalChunksToGen.toFloat()).times(100).roundToInt()
            plugin.server.broadcastMessage("${ChatColor.GREEN}Generating chunks... $chunksGenerated/$totalChunksToGen ($perc%)")

            chunksToUnload.forEach {
                world.unloadChunk(it.x, it.z, true)
            }

            chunksToUnload.clear()

            repeat(4) {
                val thischunk = chunksToGenerate.removeAt(0)
                world.loadChunk(thischunk.x, thischunk.z, true)
                chunksToUnload.add(thischunk)
                chunksGenerated++
            }

            if (chunksToGenerate.isEmpty()) {
                plugin.server.broadcastMessage("${ChatColor.GREEN}WORLD PREGENERATION COMPLETED")
                cancel()
            }
        }
    }
}