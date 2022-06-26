package com.gamapp.domain.usecase.data.queue.favorites

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import javax.inject.Inject

class TrackIsFavoriteUseCase @Inject constructor(private val repository: FavoriteRepository) {
    fun invoke(track: TrackModel): LiveData<Boolean> {
        return repository.isFavorite(track.id)
    }
}