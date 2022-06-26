package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerConnector
import javax.inject.Inject

class FastRewindUseCase @Inject constructor(private val player: PlayerConnector) {
    suspend fun invoke() {
        player.previousMusic()
    }
}