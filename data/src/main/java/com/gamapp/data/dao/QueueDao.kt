package com.gamapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.gamapp.data.entity.QueueEntity
import com.gamapp.data.entity.QueueTrackCrossRef
import com.gamapp.data.entity.QueueWithTracks
import com.gamapp.data.entity.TrackWithQueues
import kotlinx.coroutines.flow.Flow

@Dao
interface QueueDao {
    /**
     * insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queueEntity: QueueEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertWithIgnore(queueEntity: QueueEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(queueEntity: QueueEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(queues: List<QueueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueTrackCrossRef(crossRef: QueueTrackCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueTracksCrossRef(crossRefs: List<QueueTrackCrossRef>)

    /**
     * select
     */
    @Query("SELECT * FROM QueueEntity where queueId == :id")
    suspend fun getQueue(id: String): QueueEntity


    @Query("select * from QueueEntity")
    fun getAllQueuesViaFlow(): Flow<List<QueueEntity>>

    @Query("select * from QueueEntity")
    suspend fun getAllQueues(): List<QueueEntity>

    @Transaction
    @Query("SELECT * FROM QueueEntity WHERE queueId == :id ")
    suspend fun getTracksOfQueue(id:String): QueueWithTracks?

    @Transaction
    @Query("SELECT * FROM QueueEntity")
    fun getAllTracksOfQueueViaFlow(): Flow<List<QueueWithTracks>>

    @Transaction
    @Query("SELECT * FROM TrackEntity WHERE trackId == :fileId ")
    suspend fun getQueuesOfTrack(fileId: Long): TrackWithQueues?

    @Query("SELECT COUNT(title) FROM QueueEntity WHERE title == :title")
    suspend fun numberOfQueuesWithThisTitle(title: String): Int


    @Query("SELECT COUNT(trackId) FROM QueueTrackCrossRef WHERE queueId == 'Favorite' AND trackId ==:fileId ")
    fun isFavorite(fileId: Long): Flow<Int>


    @Transaction
    @Query("SELECT * FROM QueueEntity WHERE queueId == :id ")
    fun getTracksOfQueueViaFlow(id: String): Flow<QueueWithTracks>

    @Query("SELECT COUNT(trackId) FROM QueueTrackCrossRef WHERE queueId == :id")
    fun getQueueTracksCountWithLiveData(id: String): LiveData<Int>

    @Query("SELECT COUNT(trackId) FROM QueueTrackCrossRef WHERE queueId == :id")
    fun getQueueTracksCount(id: String): Int



    @Query("SELECT trackId FROM QueueTrackCrossRef WHERE queueId ==:id ORDER BY trackId ASC LIMIT 1 ")
    fun getQueueCoverImageIdWithLiveData(id: String): LiveData<Long?>

    @Query("SELECT trackId FROM QueueTrackCrossRef WHERE queueId ==:id ORDER BY trackId ASC LIMIT 1 ")
    fun getQueueCoverImageId(id: String): Long?

    /**
     * delete
     */
    @Query("delete from QueueEntity where queueId in (:id)")
    suspend fun deleteById(id: String)

    @Delete
    suspend fun delete(list:List<QueueEntity>)


    @Delete
    suspend fun deleteQueueTrackCrossRef(crossRef: QueueTrackCrossRef)

    @Delete
    suspend fun deleteQueueTrackCrossRef(crossRefs: List<QueueTrackCrossRef>)


    @Query("Delete from QueueTrackCrossRef where queueId == :queueId")
    suspend fun clearQueueTracks(queueId: String)


}