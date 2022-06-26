package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class RemoveTrackFromQueueUseCase @Inject constructor(private val queueRepository: QueueRepository) {

    suspend fun invoke(queue: QueueModel, track: TrackModel) {
        queueRepository.removeTrackFromQueue(queue.id, track.id)
    }

    suspend fun invoke(queue: QueueModel, tracks: List<TrackModel>) {
        queueRepository.removeTracksFromQueue(queue.id, tracks = tracks)
    }
}