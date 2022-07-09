package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import com.gamapp.domain.player_interface.RepeatMode
import javax.inject.Inject

class RepeatModeUseCase @Inject constructor(
    private val events: PlayerEvents,
    private val controller: PlayerController
) {
    operator fun invoke() {
        val mode = when (events.repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
            else -> RepeatMode.OFF
        }
        invoke(mode)
    }
    operator fun invoke(mode: RepeatMode) {
        controller.setRepeatMode(mode)
    }
}