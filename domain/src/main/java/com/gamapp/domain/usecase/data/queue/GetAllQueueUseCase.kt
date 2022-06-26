package com.gamapp.domain.usecase.data.queue

import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.repository.QueueRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllQueueUseCase @Inject constructor(private val repository: QueueRepository) {
    fun invoke(): Flow<List<QueueModel>> {
        return repository.getAllQueuesViaFlow()
    }
    suspend fun invoke(id:String):QueueModel {
        return repository.getQueue(id)
    }
}