package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.repository.QueueRepository
import javax.inject.Inject

class RemoveQueueUseCase @Inject constructor(private val queueRepository: QueueRepository) {
    suspend fun invoke(queue: QueueModel){
        queueRepository.removeQueue(queue.id)
    }


    suspend fun invoke(queues:List<QueueModel>){
        queueRepository.removeQueues(queues)
    }

}