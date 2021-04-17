package joeshuff.plugins.uhcbase.commands

import joeshuff.plugins.uhcbase.UHCBase
import joeshuff.plugins.uhcbase.commands.base.*
import joeshuff.plugins.uhcbase.commands.teams.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

fun UHCBase.getCommandController() = CommandController(this)

fun Command.notifyCorrectUsage(sender: CommandSender): Boolean {
    sender.sendMessage("${ChatColor.RED}Incorrect usage: ${usage.replace("<command>", this.label)}")
    return true
}

class CommandController(val plugin: UHCBase) {

    fun registerCommands() {
        plugin.getCommand("edituhc")?.setExecutor(EditUHCCommand(plugin))
        plugin.getCommand("edituhc")?.tabCompleter = EditUHCCommand(plugin)

        plugin.getCommand("worldtp")?.setExecutor(WorldTpCommand(plugin))

        plugin.getCommand("pregen")?.setExecutor(PregenerateChunksCommand(plugin))
        plugin.getCommand("stop-pregen")?.setExecutor(StopPregenerationCommand(plugin))

        plugin.getCommand("recolor")?.setExecutor(RecolorCommand(plugin))

        plugin.getCommand("clearteams")?.setExecutor(ClearTeamsCommand(plugin))

        plugin.getCommand("createteam")?.setExecutor(CreateTeamCommand(plugin))

        plugin.getCommand("genteams")?.setExecutor(RandomTeamsCommand(plugin))

        plugin.getCommand("tchat")?.setExecutor(TeamChatCommand(plugin))

        plugin.getCommand("tloc")?.setExecutor(TeamLocationCommand(plugin))
        plugin.getCommand("tl")?.setExecutor(TeamLocationCommand(plugin))

        plugin.getCommand("removeteam")?.setExecutor(RemoveTeamCommand(plugin))
        plugin.getCommand("teamname")?.setExecutor(TeamNameCommand(plugin))

        plugin.getCommand("worldtest")?.setExecutor(WorldTestCommand(plugin))

        plugin.getCommand("helpop")?.setExecutor(HelpOpCommand(plugin))

        plugin.getCommand("pvp")?.setExecutor(PVPToggleCommand(plugin))
        plugin.getCommand("pvp")?.tabCompleter = PVPToggleCommand(plugin)

        plugin.getCommand("rules")?.setExecutor(RulesCommand(plugin))

        plugin.getCommand("showkills")?.setExecutor(ShowKillsCommand(plugin))
        plugin.getCommand("start-uhc")?.setExecutor(StartUhcCommand(plugin))

        plugin.getCommand("prepuhc")?.setExecutor(PrepUhcCommand(plugin))
        plugin.getCommand("stop-uhc")?.setExecutor(StopUhcCommand(plugin))

        plugin.getCommand("loc")?.setExecutor(GenerateLocationsCommand(plugin))
    }

}

