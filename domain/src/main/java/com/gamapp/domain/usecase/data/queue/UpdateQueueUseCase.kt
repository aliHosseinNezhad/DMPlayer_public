package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.repository.Event
import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class UpdateQueueUseCase @Inject constructor(private val repository: QueueRepository) {
    suspend fun invoke(id: String, title: String): Event<Unit> {
        return repository.updateQueue(id, title)
    }
}