package com.gamapp.domain.repository

import androidx.lifecycle.LiveData
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.QueueModel
import kotlinx.coroutines.flow.Flow

interface QueueRepository {
    /**
     * insert
     */
    suspend fun createQueue(title: String): Event<Unit>
    suspend fun updateQueue(id:String,title: String):Event<Unit>
    suspend fun addTrackToQueue(id: String, trackModel: TrackModel)
    suspend fun addTracksToQueue(id: String, list: List<TrackModel>)

    /**
     * delete
     */
    suspend fun removeQueue(id: String)
    suspend fun removeQueues(queues:List<QueueModel>)
    suspend fun clearTracksFromQueue(queueId: String)
    suspend fun removeTracksFromQueue(queueId: String, tracks: List<TrackModel>)
    suspend fun removeTrackFromQueue(queueId: String, trackId: Long)

    /**
     * select
     */
    fun getAllMusicByQueueIdViaFlow(queueId: String): Flow<List<TrackModel>>
    suspend fun getAllMusicByQueueId(queueId: String): List<TrackModel>
    fun getAllQueuesViaFlow(): Flow<List<QueueModel>>
    suspend fun getAllQueuesViaLiveData(): LiveData<List<QueueModel>>
    suspend fun getAllQueues(): List<QueueModel>
    suspend fun getQueue(id: String): QueueModel



    /**
     * update by media store change
     * */
    suspend fun updateTracksByMediaStoreChange(newTracks:List<TrackModel>)
}