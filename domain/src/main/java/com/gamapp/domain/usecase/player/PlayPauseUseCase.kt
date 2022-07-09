package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import javax.inject.Inject

class PlayPauseUseCase @Inject constructor(
    private val controller: PlayerController,
    private val events: PlayerEvents
) {
    operator fun invoke() {
        if (events.isPlaying.value) {
            controller.pause()
        } else controller.play()
    }
}