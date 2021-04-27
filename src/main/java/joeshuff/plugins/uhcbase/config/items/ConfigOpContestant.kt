package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem
import joeshuff.plugins.uhcbase.utils.updatePlayerFlight

class ConfigOpContestant(val controller: ConfigController): ConfigItem<Boolean>(controller, "op-contestant", true) {
    override fun onSet(value: Boolean) {
        controller.game.updatePlayerFlight()
    }

    override fun canSet(game: UHC) = game.state == UHC.GAME_STATE.PRE_GAME
}