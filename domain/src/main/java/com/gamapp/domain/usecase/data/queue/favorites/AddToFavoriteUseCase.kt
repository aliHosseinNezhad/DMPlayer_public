package com.gamapp.domain.usecase.data.queue.favorites

import androidx.lifecycle.asFlow
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val getTracksByIdUseCase: GetTracksByIdUseCase
) {
    suspend fun invoke(track: BaseTrack) {
        val id = track.id
        var isFavorite: Boolean = false
        favoriteRepository.isFavorite(id).asFlow().take(1).collect {
            isFavorite = it
        }
        invoke(track = track, favorite = !isFavorite)
    }

    suspend fun invoke(track: BaseTrack, favorite: Boolean) {
        if (favorite) {
            getTracksByIdUseCase(track.id)?.let {
                favoriteRepository.addToFavorite(it)
            }
        } else {
            favoriteRepository.removeFromFavorite(track.id)
        }
    }
}