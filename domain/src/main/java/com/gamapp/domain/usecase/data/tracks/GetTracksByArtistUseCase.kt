package com.gamapp.domain.usecase.data.tracks

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.ArtistModel
import com.gamapp.domain.repository.TrackRepository
import javax.inject.Inject

class GetTracksByArtistUseCase @Inject constructor(private val trackRepository: TrackRepository) {
    fun invoke(artist: ArtistModel): LiveData<ArtistModel> {
        return trackRepository.artist(artist.id)
    }
}