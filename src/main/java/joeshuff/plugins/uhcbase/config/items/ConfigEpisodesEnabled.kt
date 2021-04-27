package joeshuff.plugins.uhcbase.config.items

import joeshuff.plugins.uhcbase.UHC
import joeshuff.plugins.uhcbase.config.ConfigController
import joeshuff.plugins.uhcbase.config.ConfigItem

class ConfigEpisodesEnabled(controller: ConfigController): ConfigItem<Boolean>(controller, "show-episodes", true) {
    override fun onSet(value: Boolean) {

    }

    override fun canSet(game: UHC) = game.state < UHC.GAME_STATE.IN_GAME

}