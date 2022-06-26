package com.gamapp.domain.usecase.data.search

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksBySearchUseCase @Inject constructor(private val repository: SearchRepository) {
    fun invoke(): Flow<List<TrackModel>> {
        return repository.tracks()
    }
}