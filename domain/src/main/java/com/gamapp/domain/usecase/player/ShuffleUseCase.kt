package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import javax.inject.Inject

class ShuffleUseCase @Inject constructor(
    private val events:PlayerEvents,
    private val controller: PlayerController
){
    suspend operator fun invoke(){
        invoke(!events.shuffle.value)
    }

    suspend operator fun invoke(enable: Boolean){
        controller.shuffle(enable)
    }
}