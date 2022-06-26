package com.gamapp.domain.usecase.data.artist

import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.usecase.data.tracks.RemoveTrackUseCase
import javax.inject.Inject

class RemoveArtistUseCase @Inject constructor(private val trackUseCase: RemoveTrackUseCase) {
    suspend fun invoke(artists: List<ArtistModel>) {
        val tracks = artists.flatMap { it.tracks }
        trackUseCase.invoke(tracks)
    }
}