package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class AddMusicsToQueueUseCase @Inject constructor(private val queueRepository: QueueRepository) {
    suspend fun invoke(queueId: String, trackModel: TrackModel) {
        queueRepository.addTrackToQueue(queueId, trackModel)
    }

    suspend fun invoke(queueId: String, tracks: List<TrackModel>) {
        queueRepository.addTracksToQueue(queueId, tracks)
    }
}