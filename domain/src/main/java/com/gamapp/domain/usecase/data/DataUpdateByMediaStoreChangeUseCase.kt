package com.gamapp.domain.usecase.data

import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.repository.TrackRepository
import javax.inject.Inject

class DataUpdateByMediaStoreChangeUseCase @Inject constructor(
    private val repository: QueueRepository
){
    suspend operator fun invoke(tracks:List<TrackModel>){
        repository.updateTracksByMediaStoreChange(tracks)
    }
}