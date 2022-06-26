package com.gamapp.domain.usecase.data.queue.favorites

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class RemoveTrackFromFavoriteUseCase @Inject constructor(private val repository: FavoriteRepository) {
    suspend fun invoke(track:TrackModel){
        repository.removeFromFavorite(track.id)
    }

    suspend fun invoke(tracks:List<TrackModel>){
        repository.removeFromFavorite(tracks)
    }
}