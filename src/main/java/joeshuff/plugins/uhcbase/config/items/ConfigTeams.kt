package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigTeams(controller: ConfigController):
    ConfigItem<Boolean>(controller, "teams", true, true) {

    override fun onSet(value: Boolean) {

    }

    override fun canSet(game: UHC) = game.state == UHC.GAME_STATE.PRE_GAME
}