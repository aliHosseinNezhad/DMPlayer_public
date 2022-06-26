package com.gamapp.domain.usecase.data.queue.favorites

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteTracksUseCase @Inject constructor(private val repository: TrackRepository) {
    fun invoke(): LiveData<List<TrackModel>> {
        return repository.trackByFavorite()
    }
}