package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class ClearQueueUseCase @Inject constructor(private val repository: QueueRepository) {
    suspend fun invoke(queue: QueueModel) {
        repository.clearTracksFromQueue(queue.id)
    }
}