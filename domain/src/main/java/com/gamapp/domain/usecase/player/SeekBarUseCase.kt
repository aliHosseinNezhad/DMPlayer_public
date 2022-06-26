package com.gamapp.domain.usecase.player


import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.PlayerData
import kotlinx.coroutines.flow.take
import javax.inject.Inject


class SeekBarUseCase @Inject constructor(
    private val player: PlayerConnector,
    private val playerData: PlayerData
) {
    suspend fun invoke(progress: Float) {
        val seekTo = (playerData.duration.value * progress).toLong()
        player.seekTo(seekTo)
        playerData.currentPosition.take(2).collect {}
    }
}