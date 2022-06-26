package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class CreateQueueUseCase @Inject constructor(private val repository: QueueRepository) {
    suspend fun invoke(title: String) {
        repository.createQueue(title)
    }
}