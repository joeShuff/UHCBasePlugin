package joeshuff.plugins.uhcbase.permissions

import org.bukkit.permissions.Permission
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin

class Permissions {
    companion object {

        fun initialisePermissions(plugin: JavaPlugin) {
            val startedPermission = Permission("blockBefore.allowed")

            var manager: PluginManager = plugin.server.pluginManager
            manager.addPermission(startedPermission)
        }

    }
}