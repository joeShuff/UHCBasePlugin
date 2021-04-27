package joeshuff.plugins.uhcbase.gamemodes

import joeshuff.plugins.uhcbase.UHC
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.entity.ThrownPotion
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
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.util.Vector
import java.util.*

class FlowerPower(override val game: UHC): Listener, GamemodeController(game) {

    val allMaterials = arrayListOf<Material>()

    val DOUBLES = listOf(Material.ROSE_BUSH, Material.LILAC, Material.SUNFLOWER, Material.PEONY)

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
        Material.FROSTED_ICE,
        Material.DEBUG_STICK,
        Material.TRIPWIRE,
        Material.TALL_SEAGRASS,
        Material.TALL_GRASS,
        Material.CARROTS,
        Material.END_GATEWAY,
        Material.WEEPING_VINES_PLANT,
        Material.SWEET_BERRY_BUSH,
        Material.REDSTONE_WIRE
    )

    val flowerTypes = listOf(
        Material.DANDELION,
        Material.ROSE_BUSH,
        Material.BLUE_ORCHID,
        Material.OXEYE_DAISY,
        Material.POPPY,
        Material.PEONY,
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

    override fun isEnabled(): Boolean {
        return game.configController.loadConfigFile("modes")?.getBoolean("flower-power")?: false
    }

    override fun gameTick() {}

    override fun playerDeath(player: Player) {}

    override fun onGameStateChange(newState: UHC.GAME_STATE) {
        if (!isEnabled()) return

        if (newState == UHC.GAME_STATE.IN_GAME) {
            onGameStart()
        }

        if (newState == UHC.GAME_STATE.VICTORY_LAP) {
            onGameEnd()
        }
    }

    override fun onEpisodeChange(episodeNumber: Int) {}

    fun onGameStart() {
        plugin.server.pluginManager.registerEvents(this, plugin)
        plugin.server.broadcastMessage("Gamemode §cF§aL§bO§6W§dE§eR §rPower §a§lEnabled")

        allMaterials.addAll(Material.values())
        allMaterials.removeAll(NOT)
        allMaterials.removeAll(DOUBLES)
    }

    fun onGameEnd() {
        HandlerList.unregisterAll(this)
    }

    @EventHandler
    fun breakBlock(event: BlockBreakEvent) {
        val player = event.player
        val pos = event.block.location.add(0.5, 0.0, 0.5)
        val brokenBlock = event.block

        if (flowerTypes.contains(brokenBlock.type)) {
            val rnd = Random()
            val chosen = rnd.nextInt(10000)

            if (chosen > 2) {
                try {
                    getItem(player, pos)?.let {
                        player.world.dropItemNaturally(pos, it)
                    }

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
        if (DOUBLES.contains(event.entity.itemStack.type)) {
            event.isCancelled = true
            event.location.block.type = Material.AIR
        }
    }

    private fun getItem(player: Player, position: Location): ItemStack? {
        var randomMaterial = allMaterials.random()

        randomMaterial = fixMaterialIfNotValid(randomMaterial)?: return getItem(player, position)

        if (randomMaterial.toString().contains("LEGACY") || NOT.contains(randomMaterial)) {
            plugin.logger.info("Material IGNORED is $randomMaterial")
            return getItem(player, position)
        }

        val stack = ItemStack(randomMaterial, 1)

        if (stack.type == Material.LINGERING_POTION || stack.type == Material.SPLASH_POTION) {
            val potionEffect = PotionEffectType.values().random()
            val meta = stack.itemMeta as PotionMeta
            meta.addCustomEffect(PotionEffect(potionEffect, 2000, 1, true, true), true)
            stack.itemMeta = meta

            val potion = position.world?.spawnEntity(position, EntityType.SPLASH_POTION) as ThrownPotion
            potion.velocity = Vector(0.0, 0.75, 0.0)
            potion.item = stack
            return null
        }

        plugin.logger.info("Material Chosen is " + stack.type.toString())

        if (stack.type == Material.ENCHANTED_BOOK) {
            var meta = stack.itemMeta as EnchantmentStorageMeta
            val amountofEnchs = Random().nextInt(3) + 1
            for (i in 0 until amountofEnchs) {
                meta = addEnchant(meta)
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
            val pages = Random().nextInt(3) + 1
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

    private fun addEnchant(meta: EnchantmentStorageMeta): EnchantmentStorageMeta {
        val enchantment = Enchantment.values().random()
        meta.addStoredEnchant(enchantment, Random().nextInt(3) + 1, true)
        return meta
    }

    private fun getPage(): String {
        return ""
    }
}