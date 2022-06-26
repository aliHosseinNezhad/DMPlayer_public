package com.gamapp.domain.usecase.data.queue.favorites

import androidx.lifecycle.asFlow
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class AddToFavoriteUseCase @Inject constructor(private val favoriteRepository: FavoriteRepository) {
    suspend fun invoke(track: TrackModel) {
        val id = track.id
        var isFavorite: Boolean = false
        favoriteRepository.isFavorite(id).asFlow().take(1).collect {
            isFavorite = it
        }
        invoke(track = track, favorite = !isFavorite)
    }

    suspend fun invoke(track: TrackModel, favorite: Boolean) {
        if (favorite)
            favoriteRepository.addToFavorite(track)
        else favoriteRepository.removeFromFavorite(track.id)
    }
}