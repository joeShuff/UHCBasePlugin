package joeshuff.plugins.uhcbase.datatracker

import joeshuff.plugins.uhcbase.UHCPlugin

class DataTracker(plugin: UHCPlugin) {

//    private var firstDamage = false
//    var tableName = ""
//    var playerInfo = HashMap<String, PlayerInfo>()
//
//    init {
//        for (p in Bukkit.getOnlinePlayers()) {
//            playerInfo[p.name] = PlayerInfo(p.name)
//        }
//    }
//
//    private val walkStats: Unit
//        private get() {
//            val obj = Bukkit.getServer().scoreboardManager!!.mainScoreboard.getObjective("walking")
//            for (p in playerInfo.keys) {
//                if (playerInfo[p]!!.distance == -1) {
//                    val s = obj!!.getScore(p)
//                    playerInfo[p]!!.distance = s.score / 100
//                }
//            }
//        }
//
//    class PlayerInfo(playername: String) {
//        private val playername = ""
//        private var damage = 0
//        private var kills = 0
//        private var killer = "No-one"
//        private var gapples = 0
//        private var block_placed = 0
//        private var block_broken = 0
//        var distance = -1
//        private var pveKills = 0
//        private var coal_Mined = 0
//        private var iron_Mined = 0
//        private var gold_Mined = 0
//        private var diamond_Mined = 0
//        private var lapis_Mined = 0
//        private var emerald_Mined = 0
//        private var redstone_Mined = 0
//        private var first_damage = false
//        private var ironMan = false
//
//        fun tookDamage(damage: Double) {
//            this.damage += damage.toInt()
//            if (!firstDamage) {
//                first_damage = true
//                firstDamage = true
//            }
//            var lastPlayer = ""
//            var noDamCount = 0
//            for (p in playerInfo.keys) {
//                if (playerInfo[p]!!.damage == 0) {
//                    noDamCount++
//                    lastPlayer = p
//                }
//            }
//            if (noDamCount == 1) {
//                playerInfo[lastPlayer]!!.ironMan = true
//            }
//        }
//
//        fun gotKill() {
//            kills++
//        }
//
//        fun killed(killer: String) {
//            this.killer = killer
//            val obj = Bukkit.getServer().scoreboardManager!!.mainScoreboard.getObjective("walking")
//            val s = obj!!.getScore(playername)
//            playerInfo[playername]!!.distance = s.score / 100
//        }
//
//        fun gappleConsumed() {
//            gapples++
//        }
//
//        fun blockPlaced() {
//            block_placed++
//        }
//
//        fun blockBroken() {
//            block_broken++
//        }
//
//        fun minedBlock(mat: Material) {
//            if (mat == Material.COAL_ORE) {
//                coal_Mined++
//            } else if (mat == Material.IRON_ORE) {
//                iron_Mined++
//            } else if (mat == Material.DIAMOND_ORE) {
//                diamond_Mined++
//            } else if (mat == Material.GOLD_ORE) {
//                gold_Mined++
//            } else if (mat == Material.LAPIS_ORE) {
//                lapis_Mined++
//            } else if (mat == Material.EMERALD_ORE) {
//                emerald_Mined++
//            } else if (mat == Material.REDSTONE_ORE) {
//                redstone_Mined++
//            }
//        }
//
//        fun entityKill() {
//            pveKills++
//        }
//
//        init {
//            this.playername = playername
//        }
//    }
}