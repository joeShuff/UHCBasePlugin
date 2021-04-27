package joeshuff.plugins.uhcbase.commands.base

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.commands.notifyCorrectUsage
import joeshuff.plugins.uhcbase.commands.notifyInvalidPermissions
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player

class EditUHCCommand(val game: UHC) : TabExecutor {

    val plugin = game.plugin

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && !sender.isOp) {
            return command.notifyInvalidPermissions(sender)
        }

        if (args.isEmpty()) {
            return command.notifyCorrectUsage(sender)
        }

        val configItem = game.configController.getConfigItem(args[0])

        if (args.size == 1) {
            configItem?.let {
                sender.sendMessage("${ChatColor.AQUA.toString() + ChatColor.BOLD}${it.configKey}: ${it.get()}" )
            }?: run {
                sender.sendMessage("${ChatColor.RED}Cannot find config item for ${args[0]}")
            }

            return true
        }

        if (args.size == 2) {
            configItem?.let {
                if (!it.canSet(game)) {
                    sender.sendMessage("${ChatColor.RED}Cannot change this item right now.")
                    return true
                }

                val newvalue = args[1].toLowerCase()

                when (it.getDefault()) {
                    is Boolean -> {
                        if (newvalue !in listOf("true", "false")) {
                            sender.sendMessage("${ChatColor.RED}Invalid option. Please enter true or false")
                            return true
                        } else {
                            it.set(newvalue == "true")
                        }
                    }
                    is Double -> {
                        if (it.isValid(newvalue.toDoubleOrNull())) {
                            it.set(newvalue.toDouble())
                        } else {
                            sender.sendMessage("${ChatColor.RED}Invalid option. Please enter ${it.getLimits()}")
                            return true
                        }
                    }
                    is Int -> {
                        if (it.isValid(newvalue.toIntOrNull())) {
                            it.set(newvalue.toInt())
                        } else {
                            sender.sendMessage("${ChatColor.RED}Invalid option. Please enter ${it.getLimits()}")
                            return true
                        }
                    }
                }

                var rule = args[0].split("-").joinToString(" ").capitalize().trim()
                val updatedMessage = "${ChatColor.GREEN}Rule ${ChatColor.RED.toString() + ChatColor.ITALIC.toString()}$rule ${ChatColor.RESET.toString() + ChatColor.GREEN}updated to: ${ChatColor.AQUA}${args[1]}"

                if (it.announceChange()) {
                    Bukkit.broadcastMessage(updatedMessage)
                } else {
                    sender.sendMessage(updatedMessage)
                }
            }?: run {
                sender.sendMessage("${ChatColor.RED}Cannot find config item for ${args[0]}")
                return true
            }
        }

        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (sender is Player) {
            if (!sender.isOp) return emptyList()
        }

        when (args.size) {
            1 -> {
                return game.configController.configItems.map { it.configKey }.filter { args[0] in it }
            }
            2 -> {
                game.configController.getConfigItem(args[0])?.let {
                    if (it.get() is Boolean) {
                        return listOf("true", "false")
                    }
                }?: return emptyList()
            }
        }

        return emptyList()
    }
}