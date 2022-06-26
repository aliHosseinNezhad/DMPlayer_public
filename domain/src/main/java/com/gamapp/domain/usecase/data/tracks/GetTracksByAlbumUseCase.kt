package com.gamapp.domain.usecase.data.tracks

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.AlbumModel
import com.gamapp.domain.repository.TrackRepository
import javax.inject.Inject

class GetTracksByAlbumUseCase @Inject constructor(private val trackRepository: TrackRepository) {
    fun invoke(album: AlbumModel): LiveData<AlbumModel> {
        return trackRepository.album(album.id)
    }
}