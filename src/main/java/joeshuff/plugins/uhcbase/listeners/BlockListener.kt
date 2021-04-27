package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class BlockListener(val game: UHC): Listener, Stoppable {

    val leaves = arrayListOf(Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES)

    val plugin = game.plugin

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    override fun stop() {
        HandlerList.unregisterAll(this)
    }

    private fun potentialAppleSpawn(location: Location): Boolean {
        if (Random.nextDouble(0.0, 100.0) <= game.configController.APPLE_RATE.get()) {
            location.world?.dropItemNaturally(location, ItemStack(Material.APPLE, 1))
            return true
        }

        return false
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.isCancelled) return

        if (event.block.type in leaves) {
            if (potentialAppleSpawn(event.block.location)) {
                event.isCancelled = true
                event.block.type = Material.AIR
            }
        }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        if (game.state != UHC.GAME_STATE.IN_GAME) {
            event.isCancelled = true
            return
        }

        if (potentialAppleSpawn(event.block.location)) {
            event.isCancelled = true
            event.block.type = Material.AIR
        }
    }

    @EventHandler
    fun brewingStand(event: BrewEvent) {
        try {
            var glowstone = false
            var strength1 = false
            var ghasttear = false

            for (items in event.contents.contents) {
                if (items.type == Material.POTION && (items.durability.toInt() == 8265 || items.durability.toInt() == 8201 || items.durability.toInt() == 16393 || items.durability.toInt() == 16457)) {
                    strength1 = true
                }
                if (items.type == Material.GLOWSTONE_DUST) {
                    glowstone = true
                }
                if (items.type == Material.GHAST_TEAR) {
                    ghasttear = true
                }
            }
            if (strength1 && glowstone || ghasttear) {
                event.isCancelled = true
            }
        } catch (e: Exception) {
        }
    }
}