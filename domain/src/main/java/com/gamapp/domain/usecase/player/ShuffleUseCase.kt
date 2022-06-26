package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import javax.inject.Inject


class ShuffleUseCase @Inject constructor(
    private val playerData: PlayerData,
    private val player: PlayerConnector
) {
     suspend fun invoke() {
         val toShuffle = !playerData.shuffle.value
         invoke(enable = toShuffle)
    }

    suspend fun invoke(enable: Boolean) {
        player.shuffleMode(enable)
    }
}