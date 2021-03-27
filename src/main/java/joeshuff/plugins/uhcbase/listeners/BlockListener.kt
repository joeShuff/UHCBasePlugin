package joeshuff.plugins.uhcbase.listeners

import joeshuff.plugins.uhcbase.Constants
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.getConfigController
import joeshuff.plugins.uhcbase.datatracker.DataTracker
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.block.LeavesDecayEvent
import org.bukkit.event.inventory.BrewEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin
import kotlin.random.Random

class BlockListener(val plugin: JavaPlugin): Listener {

    init {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }

    private fun potentialAppleSpawn(location: Location) {
        if (Random.nextDouble(0.0, 100.0) <= plugin.getConfigController().APPLE_RATE.get()) {
            location.world?.dropItemNaturally(location, ItemStack(Material.APPLE, 1))
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (player.world.name == Constants.hubWorldName && !player.isOp) {
            event.isCancelled = true
            return
        }

        if (!player.hasPermission("blockBefore.allowed")) {
            player.sendMessage("" + ChatColor.RED + "Unable to destroy block before UHC started!")
            event.isCancelled = true
            return
        }

        val leaves = arrayListOf(Material.ACACIA_LEAVES, Material.BIRCH_LEAVES, Material.DARK_OAK_LEAVES, Material.JUNGLE_LEAVES, Material.OAK_LEAVES, Material.SPRUCE_LEAVES)

        if (event.block.type in leaves) {
            potentialAppleSpawn(event.block.location)
        }
    }

    @EventHandler
    fun onLeavesDecay(event: LeavesDecayEvent) {
        potentialAppleSpawn(event.block.location)
    }

    @EventHandler
    fun placeBlock(event: BlockPlaceEvent) {
        val player = event.player

        if (player.isOp) return

        if (!player.hasPermission("blockBefore.allowed")) {
            player.sendMessage("" + ChatColor.RED + "Unable to place block before UHC has started!")
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