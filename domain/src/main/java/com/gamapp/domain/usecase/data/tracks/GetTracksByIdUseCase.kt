package com.gamapp.domain.usecase.data.tracks

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.TrackRepository
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class GetTracksByIdUseCase @Inject constructor(
    private val repository: TrackRepository
) {
    suspend operator fun invoke(id: Long): TrackModel? {
        var track: TrackModel? = null
        repository.getTrackById(id).take(1).collect {
            track = it
        }
        return track
    }

    suspend operator fun invoke(ids: List<Long>): List<TrackModel> {
        val tracks: List<TrackModel>
        coroutineScope {
            tracks = repository.getTracksById(ids).take(1).stateIn(this).value
        }
        return tracks
    }
}