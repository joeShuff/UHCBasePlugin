# UHCBasePlugin
UHCBase is a plugin for Spigot Minecraft servers to control a game of UHC (Ultra Hardcore).

# Installation
Download the latest version for your minecraft version from the releases section. When later versions of Minecraft release, I will begin further development in that version only, so any bugs won't be fixed for older versions of Minecraft.

Current Version
[AWAITING TESTING FOR FIRST BUILD]

# Playing Instructions
At the absolute minimum, there are 2 commands that need to be ran to start your UHC.

`/prepuhc <worldborder diameter>` 
This command will do all the world preparation it needs and then spread the players/teams out randomly in the world. (See config for instructions for teams/solo spreading)

`/start-uhc`
This command kicks it all off, a countdown of 10 seconds will be following by the Ender Dragons roar kicking off the UHC.

# Config
Check out the config options in [CONFIG.md](https://github.com/joeShuff/UHCBasePlugin/blob/main/CONFIG.md)

# Gamemodes
This plugin supports some custom UHC gamemodes, information about these can be found in [GAMEMODES.md](https://github.com/joeShuff/UHCBasePlugin/blob/main/GAMEMODES.md)

# Planned Features
- Pregeneration of chunks
- Player heads on death + craft golden head
- Disabling attack cooldown option
- Forcing surface at a set episode (will have countdown)
- Clearing nether at a set episode (auto teleport will need to be safe guaranteed)
- More gamemodes obviously

# Terminology
Throughout the description of how to use this plugin I will use some concepts that could be interpreted in a few ways. This defines the way **this** plugin understands these concepts to hopefully clear up any confusion.

### Episode
Internally an episode is really just a "unit of time", defined by the config item `episode-length`. Most events in the UHC that are triggered by time are configured in terms of episodes. So if you want the world border to shrink after 40 minutes, you can set an `episode-length` of 40 minutes and a `shrink-ep` of 2.

*Episode 1 starts immediately as the UHC starts so configuring events to happen at Episode 1 will happen right away* 

### Perma Day
Perma day is a timed event that sets the time to 0 and disables the game rule `doDaylightCycle`. When this happens can be configured using `perma-day-ep` config item.

### Grace Period
Grace period is a time when PVP is disabled on all worlds (overworld, nether and end). When `grade-end-ep` epsode is reached, PVP is enabled and announced to the world. To disable this set `grace-end-ep` to 1 or less. (Setting to 1 would end grace period immediately anyway, so it just disables it)

### World Border
Whilst you might not want a world border in your world it is crucial for this plugin to know how far to spread players around 0,0. Once the UHC has started an operator can manually remove the world border if they wish. You can disable shrinking by setting `shrink-ep` to 0 or less.

### Spectating
This plugin refers to "spectating" as a player being allowed into the live game worlds in spectator mode. If `can-spectate` is set to true, when a player dies they will be set into spectator mode to watch the rest of the game. (Unless kicked, see `seconds-until-kick`)

# Customise your UHC
This UHC plugin comes with support for custom "skinning" so to speak. Take a look inside the `customize.yml` file and you will find you can change some of the messages that the players will see. All [color coding](https://minecraft.fandom.com/wiki/Formatting_codes) supported using the `§` character.

Info about options coming soon 