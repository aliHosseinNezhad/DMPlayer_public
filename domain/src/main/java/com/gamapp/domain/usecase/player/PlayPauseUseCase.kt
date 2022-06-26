package com.gamapp.domain.usecase.player


import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import javax.inject.Inject



class PlayPauseUseCase @Inject constructor(
    private val player: PlayerConnector,
    private val playerData: PlayerData
) {
     suspend fun invoke() {
        if (playerData.isPlaying.value) {
            player.pause()
        } else player.play()
    }
}