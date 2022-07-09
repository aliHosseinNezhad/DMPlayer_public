package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import javax.inject.Inject


class SeekBarUseCase @Inject constructor(
    private val controller: PlayerController,
    private val events: PlayerEvents
) {
    suspend operator fun invoke(ml: Long) {
        controller.seekTo(ml)

    }
}
