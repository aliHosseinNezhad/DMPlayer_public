package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import javax.inject.Inject

//import com.gamapp.domain.player_interface.PlayerConnector
//import javax.inject.Inject

class FastRewindUseCase @Inject constructor(private val controller: PlayerController) {
    operator fun invoke(){
        controller.fastRewind()
    }
}