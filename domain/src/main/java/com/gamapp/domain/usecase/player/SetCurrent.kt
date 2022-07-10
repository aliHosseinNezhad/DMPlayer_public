package com.gamapp.domain.usecase.player

import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.player_interface.PlayerController
import javax.inject.Inject

class SetCurrentTrackUseCase @Inject constructor(private val controller:PlayerController){
    operator fun invoke(track:BaseTrack){
        controller.setCurrentTrack(track)
    }
}