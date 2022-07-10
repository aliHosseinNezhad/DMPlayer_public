package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import javax.inject.Inject

class AddMusicsToQueueUseCase @Inject constructor(
    private val queueRepository: QueueRepository,
    val getTracksByIdUseCase: GetTracksByIdUseCase
) {
    suspend operator fun invoke(queueId: String, trackModel: BaseTrack) {
        getTracksByIdUseCase(trackModel.id)?.let {
            queueRepository.addTrackToQueue(queueId, it)
        }
    }

    suspend operator fun invoke(queueId: String, tracks: List<BaseTrack>) {
        getTracksByIdUseCase(tracks.map { it.id }).let {
            queueRepository.addTracksToQueue(queueId, it)
        }
    }
}