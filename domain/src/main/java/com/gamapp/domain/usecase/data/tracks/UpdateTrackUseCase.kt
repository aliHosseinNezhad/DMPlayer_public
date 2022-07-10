package com.gamapp.domain.usecase.data.tracks

import android.content.Context
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.TrackUpdate
import com.gamapp.domain.models.toContentValue
import com.gamapp.domain.repository.AlbumRepository
import com.gamapp.domain.repository.TrackRepository
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class UpdateTrackUseCase @Inject constructor(private val repository: TrackRepository) {
    suspend fun invoke(track: BaseTrack, items: List<TrackUpdate>) {
        val contentValues = items.toContentValue()
        repository.updateTracks(listOf(track), contentValues)
    }

    suspend fun invoke(tracks: List<TrackModel>) {

    }
}