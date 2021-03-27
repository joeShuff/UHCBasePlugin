package joeshuff.plugins.uhcbase

import org.bukkit.ChatColor
import org.bukkit.entity.Player

object VisualEffects {
    fun setPlayerName(player: Player, prefix: String?) {
        setPlayerName(player, prefix, null)
    }

    fun setPlayerName(player: Player, prefix: String?, teamColor: String?) {
        val name = player.name
        val newName: String
        newName = if (prefix == null) {
            if (teamColor == null) {
                name
            } else {
                teamColor + name
            }
        } else {
            prefix + " " + name + ChatColor.WHITE
        }

        player.setDisplayName(newName)
        player.setPlayerListName(newName)
        player.customName = newName
    }
}