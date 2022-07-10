package com.gamapp.domain.usecase.data.tracks

import android.content.ContentUris
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.TrackRepository
import javax.inject.Inject


class RemoveTrackUseCase @Inject constructor(
    private val repository: TrackRepository,
) {
    companion object {
        const val TAG = "RemoveTrackUseCase"
    }

    suspend fun invoke(
        track: BaseTrack
    ) {
        repository.removeTrack(track)
    }
    suspend fun invoke(tracks: List<BaseTrack>) {
        repository.removeTracks(tracks)
    }
}