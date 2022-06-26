package com.gamapp.domain.usecase.data.tracks

import android.content.ContentUris
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
        track: TrackModel
    ) {
        repository.removeTrack(track)
    }


    suspend fun invoke(tracks: List<TrackModel>) {
        repository.removeTracks(tracks)
    }
}