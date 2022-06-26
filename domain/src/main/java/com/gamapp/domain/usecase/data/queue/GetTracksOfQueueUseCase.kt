package com.gamapp.domain.usecase.data.queue

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.QueueRepository
import com.gamapp.domain.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTracksOfQueueUseCase @Inject constructor(private val repository: TrackRepository) {
    fun invoke(queue: QueueModel): LiveData<List<TrackModel>> {
        return repository.trackByQueue(queue.id)
    }

    fun invoke(queueId: String): LiveData<List<TrackModel>> {
        return repository.trackByQueue(queueId)
    }
}