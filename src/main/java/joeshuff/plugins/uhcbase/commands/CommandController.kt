package joeshuff.plugins.uhcbase.commands

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.UHCPlugin
import joeshuff.plugins.uhcbase.commands.base.*
import joeshuff.plugins.uhcbase.commands.teams.*
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender

fun UHC.getCommandController() = CommandController(this)

fun Command.notifyCorrectUsage(sender: CommandSender): Boolean {
    sender.sendMessage("${ChatColor.RED}Incorrect usage: ${usage.replace("<command>", this.label)}")
    return true
}

fun Command.notifyInvalidPermissions(sender: CommandSender, message: String = "You do not have permissions to use this command."): Boolean {
    sender.sendMessage("${ChatColor.RED}$message")
    return true
}

class CommandController(val game: UHC) {

    val plugin = game.plugin

    fun registerCommands() {
        plugin.getCommand("edituhc")?.setExecutor(EditUHCCommand(game))
        plugin.getCommand("edituhc")?.tabCompleter = EditUHCCommand(game)

        plugin.getCommand("worldtp")?.setExecutor(WorldTpCommand(game))

        plugin.getCommand("pregen")?.setExecutor(PregenerateChunksCommand(game))
        plugin.getCommand("stop-pregen")?.setExecutor(StopPregenerationCommand(game))

        plugin.getCommand("recolor")?.setExecutor(RecolorCommand(game))

        plugin.getCommand("clearteams")?.setExecutor(ClearTeamsCommand(game))

        plugin.getCommand("createteam")?.setExecutor(CreateTeamCommand(game))

        plugin.getCommand("genteams")?.setExecutor(RandomTeamsCommand(game))

        plugin.getCommand("tchat")?.setExecutor(TeamChatCommand(game))

        plugin.getCommand("tloc")?.setExecutor(TeamLocationCommand(game))
        plugin.getCommand("tl")?.setExecutor(TeamLocationCommand(game))

        plugin.getCommand("removeteam")?.setExecutor(RemoveTeamCommand(game))
        plugin.getCommand("teamname")?.setExecutor(TeamNameCommand(game))

        plugin.getCommand("worldtest")?.setExecutor(WorldTestCommand(game))

        plugin.getCommand("helpop")?.setExecutor(HelpOpCommand(game))

        plugin.getCommand("pvp")?.setExecutor(PVPToggleCommand(game))
        plugin.getCommand("pvp")?.tabCompleter = PVPToggleCommand(game)

        plugin.getCommand("rules")?.setExecutor(RulesCommand(game))

        plugin.getCommand("showkills")?.setExecutor(ShowKillsCommand(game))
        plugin.getCommand("start-uhc")?.setExecutor(StartUhcCommand(game))

        plugin.getCommand("prepuhc")?.setExecutor(PrepUhcCommand(game))
        plugin.getCommand("stop-uhc")?.setExecutor(StopUhcCommand(game))

        plugin.getCommand("loc")?.setExecutor(GenerateLocationsCommand(game))
    }

}

