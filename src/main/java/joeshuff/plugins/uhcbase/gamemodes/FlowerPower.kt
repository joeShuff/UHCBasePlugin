package joeshuff.plugins.uhcbase.gamemodes

import joeshuff.plugins.uhcbase.config.getConfigController
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.BookMeta
import org.bukkit.inventory.meta.EnchantmentStorageMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class FlowerPower(val plugin: JavaPlugin): Listener, GamemodeController {

    override fun isEnabled(): Boolean {
        return plugin.getConfigController().loadConfigFile("modes")?.getBoolean("flower-power")?: false
    }

    override fun onGameStart() {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.broadcastMessage("Gamemode §cF§aL§bO§6W§dE§eR §rPower §a§lEnabled")
    }

    override fun onGameEnd() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun breakBlock(event: BlockBreakEvent) {
        val player = event.player
        val pos = event.block.location
        val brokenBlock = event.block

        val flowerTypes = listOf(
                Material.DANDELION,
                Material.ROSE_BUSH,
                Material.BLUE_ORCHID,
                Material.OXEYE_DAISY,
                Material.POPPY,
                Material.CORNFLOWER,
                Material.AZURE_BLUET,
                Material.ORANGE_TULIP,
                Material.PINK_TULIP,
                Material.RED_TULIP,
                Material.WHITE_TULIP,
                Material.LILY_OF_THE_VALLEY,
                Material.SUNFLOWER,
                Material.LILAC,
                Material.ALLIUM)

        if (brokenBlock.biome == Biome.FLOWER_FOREST) {
            return
        }

        if (flowerTypes.contains(brokenBlock.type)) {
            val rnd = Random()
            val chosen = rnd.nextInt(10000)

            if (chosen > 2) {
                try {
                    player.world.dropItemNaturally(pos, getItem(player, pos))
                    event.isCancelled = true
                    pos.block.type = Material.AIR
                } catch (e: Exception) {
                    plugin.logger.severe("Cannot drop this item. Dropping flower")
                }
            } else {
                plugin.server.broadcastMessage(ChatColor.RED.toString() + "Hell has been released upon this world...")
                player.world.spawnEntity(Location(player.world, 0.0, 150.0, 0.0), EntityType.ENDER_DRAGON)
            }
        }
    }

    @EventHandler
    fun blank(event: ItemSpawnEvent) {
        val doubleHighs = listOf(Material.ROSE_BUSH, Material.LILAC, Material.SUNFLOWER)
        if (doubleHighs.contains(event.entity.itemStack.type)) {
            event.isCancelled = true
            event.location.block.type = Material.AIR
        }
    }

    private val NOT = arrayListOf(Material.AIR,
            Material.VOID_AIR,
            Material.CAVE_AIR,
            Material.LAVA,
            Material.WATER,
            Material.PISTON_HEAD,
            Material.MOVING_PISTON,
            Material.COCOA,
            Material.END_PORTAL,
            Material.NETHER_PORTAL,
            Material.FROSTED_ICE
    )

    private fun getItem(player: Player, position: Location): ItemStack {
        val rnd = Random()
        val materials = Material::class.java.enumConstants
        var randomMaterial = materials[getRandom(0, materials.size)] ?: return getItem(player, position)

        randomMaterial = fixMaterialIfNotValid(randomMaterial)?: return getItem(player, position)

        if (randomMaterial.toString().contains("LEGACY") || NOT.contains(randomMaterial)) {
            plugin.logger.info("Material IGNORED is $randomMaterial")
            return getItem(player, position)
        }

        val stack = ItemStack(randomMaterial, 1)

        if (stack.type == Material.LINGERING_POTION || stack.type == Material.SPLASH_POTION) {
            val potionEffects = PotionEffectType.values()
            val chosenType = potionEffects[getRandom(0, potionEffects.size - 1)]
            val meta = stack.itemMeta as PotionMeta
            meta.addCustomEffect(PotionEffect(chosenType, 2000, 1, true, true), true)
            stack.itemMeta = meta
        }

        plugin.logger.info("Material Chosen is " + stack.type.toString())

        if (stack.type == Material.ENCHANTED_BOOK) {
            var meta = stack.itemMeta as EnchantmentStorageMeta
            val amountofEnchs = rnd.nextInt(3) + 1
            for (i in 0 until amountofEnchs) {
                meta = addEnchant(meta, rnd)
            }
            stack.itemMeta = meta
        }

        if (stack.type == Material.PLAYER_HEAD) {
            val meta = stack.itemMeta as SkullMeta
            meta.owningPlayer = player
            meta.setDisplayName(player.displayName + "'s Decapitated Head")
            val lore: MutableList<String> = ArrayList()
            lore.add("Slain soon by someone stronger than them.")

            meta.lore = lore
            stack.itemMeta = meta
        }

        if (stack.type == Material.WRITTEN_BOOK) {
            val meta = stack.itemMeta as BookMeta
            val pages = rnd.nextInt(3) + 1
            for (i in 0 until pages) {
                meta.addPage(getPage())
            }
            stack.itemMeta = meta
        }

        return ItemStack(stack)
    }

    private fun fixMaterialIfNotValid(material: Material): Material? {
        if (material.toString().contains("WALL_TORCH")) {
            plugin.logger.info("Turned a wall torch into a torch")
            return Material.getMaterial(material.toString().replace("WALL_TORCH", "TORCH"))
        }
        if (material.toString().contains("WALL_FAN")) {
            plugin.logger.info("Turned a wall fan into a fan")
            return Material.getMaterial(material.toString().replace("WALL_FAN", "FAN"))
        }
        if (material.toString().contains("WALL_SIGN")) {
            plugin.logger.info("Turned a wall sign into a sign")
            return Material.getMaterial(material.toString().replace("WALL_SIGN", "SIGN"))
        }
        if (material.toString().contains("POTTED_")) {
            plugin.logger.info("removed a pot from a plant")
            return Material.getMaterial(material.toString().replace("POTTED_", ""))
        }
        if (material.toString().contains("_STEM")) {
            plugin.logger.info("destemmed something")
            return Material.getMaterial(material.toString().replace("_STEM", ""))
        }
        if (material.toString().contains("WALL_BANNER")) {
            plugin.logger.info("turned a wall banner into a banner")
            return Material.getMaterial(material.toString().replace("WALL_BANNER", "BANNER"))
        }
        if (material.toString().contains("WALL_HEAD")) {
            plugin.logger.info("Turned wall head into a head")
            return Material.getMaterial(material.toString().replace("WALL_HEAD", "HEAD"))
        }

        return material
    }

    fun getRandom(a: Int, b: Int): Int {
        return Math.round(Math.random() * b + a).toInt()
    }

    private fun addEnchant(meta: EnchantmentStorageMeta, rnd: Random): EnchantmentStorageMeta {
        val enchantments = Enchantment.values()
        val random = enchantments[getRandom(0, enchantments.size - 1)]
        meta.addStoredEnchant(random, rnd.nextInt(3) + 1, true)
        return meta
    }

    private fun getPage(): String {
        return ""
    }
}