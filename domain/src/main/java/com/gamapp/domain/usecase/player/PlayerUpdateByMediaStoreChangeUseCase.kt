package com.gamapp.domain.usecase.player

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerController
import com.gamapp.domain.player_interface.PlayerEvents
import javax.inject.Inject

class PlayerUpdateByMediaStoreChangeUseCase @Inject constructor(
    private val controller: PlayerController,
    private val events: PlayerEvents
) {
    suspend operator fun invoke(tracks:List<TrackModel>) {

    }
}