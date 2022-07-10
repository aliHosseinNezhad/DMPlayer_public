package com.gamapp.domain.usecase.player

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerController
import javax.inject.Inject

class SetPlayListUseCase @Inject constructor(private val controller:PlayerController) {
    operator fun invoke(playList:List<TrackModel>,current:TrackModel) {
        controller.setPlayList(current,playList)
    }
}
class SetPlayListAndPlayUseCase @Inject constructor(private val controller:PlayerController) {
    operator fun invoke(playList:List<TrackModel>,current:TrackModel) {
        controller.setPlayListAndPlay(current,playList)
    }
}