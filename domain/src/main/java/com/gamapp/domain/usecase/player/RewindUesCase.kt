package com.gamapp.domain.usecase.player

import com.gamapp.domain.player_interface.PlayerController
import javax.inject.Inject

class RewindUseCase @Inject constructor(private val controller:PlayerController){
    suspend operator fun invoke(ml:Long){
        controller.rewind(ml)
    }
}