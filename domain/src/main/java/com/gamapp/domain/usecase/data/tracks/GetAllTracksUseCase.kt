package com.gamapp.domain.usecase.data.tracks

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.TrackRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GetAllTracksUseCase @Inject constructor(private val repository: TrackRepository) {
    operator fun invoke(): LiveData<List<TrackModel>> {
        return repository.tracks()
    }
}