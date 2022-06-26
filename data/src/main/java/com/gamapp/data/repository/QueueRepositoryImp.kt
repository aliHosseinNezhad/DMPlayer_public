package com.gamapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.gamapp.data.dao.MusicDao
import com.gamapp.data.dao.QueueDao
import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.data.entity.QueueEntity
import com.gamapp.data.entity.QueueTrackCrossRef
import com.gamapp.data.mapper.toQueueEntity
import com.gamapp.data.mapper.toQueueModel
import com.gamapp.data.mapper.toTrackEntity
import com.gamapp.data.mapper.toTrackModel
import com.gamapp.data.utils.DefaultQueueIds
import com.gamapp.data.utils.UniqueIdManager
import com.gamapp.domain.models.QueueModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.Event
import com.gamapp.domain.repository.QueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


class QueueRepositoryImp @Inject constructor(
    private val queueDao: QueueDao,
    private val musicDao: MusicDao,
    private val uniqueIdManager: UniqueIdManager,
    private val fetchDataSource: MediaStoreFetchDataSource
) : QueueRepository {
    companion object {
        const val TAG = "QueueRepository"
    }

    private suspend fun insertDefaultQueues() {
        withContext(Dispatchers.IO) {
            queueDao.insertWithIgnore(
                QueueEntity(
                    DefaultQueueIds.Favorite,
                    defaultQueue = 1,
                    title = DefaultQueueIds.Favorite
                )
            )
            queueDao.insertWithIgnore(
                QueueEntity(
                    DefaultQueueIds.Queue,
                    defaultQueue = 1,
                    title = DefaultQueueIds.Queue
                )
            )
        }

    }

    override suspend fun createQueue(title: String): Event<Unit> {
        val id = uniqueIdManager.generateId()
        if (queueDao.numberOfQueuesWithThisTitle(title) == 0)
            queueDao.insert(QueueEntity(id, title))
        else return Event.Failure(message = "already one queue with this title exist!")
        return Event.Success(Unit)
    }

    override suspend fun updateQueue(id: String, title: String): Event<Unit> {

        if (withContext(Dispatchers.IO) {
                queueDao.numberOfQueuesWithThisTitle(title)
            } != 0)
            return Event.Failure(message = "already one queue with this title exist!")
        val item = withContext(Dispatchers.IO) {
            queueDao.getQueue(id).copy(title = title)
        }
        withContext(Dispatchers.IO) {
            queueDao.update(item)
        }
        return Event.Success(Unit)
    }

    override suspend fun removeQueue(id: String) {
        if (uniqueIdManager.getAllIds().contains(id)) {
            withContext(Dispatchers.IO) {
                clearTracksFromQueue(id)
                queueDao.deleteById(id)
            }
            uniqueIdManager.removeId(id)
        }
    }

    override suspend fun removeQueues(queues: List<QueueModel>) {
        queues.map { it.toQueueEntity() }.let { it ->
            queueDao.delete(it)
            it.forEach {
                uniqueIdManager.removeId(it.id)
            }
        }
    }

    override suspend fun clearTracksFromQueue(queueId: String) {
        withContext(Dispatchers.IO) {
            queueDao.clearQueueTracks(queueId)
        }

    }

    override suspend fun removeTracksFromQueue(queueId: String, tracks: List<TrackModel>) {
        withContext(Dispatchers.IO) {
            queueDao.deleteQueueTrackCrossRef(tracks.map { QueueTrackCrossRef(queueId, it.id) })
        }
    }


    override suspend fun addTrackToQueue(id: String, trackModel: TrackModel) {
        withContext(Dispatchers.IO) {
            musicDao.insertOrIgnore(trackModel.toTrackEntity())
            queueDao.insertQueueTrackCrossRef(QueueTrackCrossRef(id, trackModel.id))
        }
    }

    override suspend fun addTracksToQueue(id: String, list: List<TrackModel>) {
        withContext(Dispatchers.IO) {
            musicDao.insertOrIgnore(list.map { it.toTrackEntity() })
            queueDao.insertQueueTracksCrossRef(list.map { QueueTrackCrossRef(id, it.id) })
        }

    }


    override suspend fun removeTrackFromQueue(queueId: String, trackId: Long) {
        withContext(Dispatchers.IO) {
            queueDao.deleteQueueTrackCrossRef(QueueTrackCrossRef(queueId, trackId))
        }

    }

    override fun getAllMusicByQueueIdViaFlow(queueId: String): Flow<List<TrackModel>> {
        return channelFlow {
            queueDao.getTracksOfQueueViaFlow(queueId).map { it ->
                it.tracks.map { it.fileId }
            }.collectLatest { ids ->
                fetchDataSource.getTracksByIds(ids).collectLatest {
                    send(it)
                }
            }
        }
    }

    override suspend fun getAllMusicByQueueId(queueId: String): List<TrackModel> {
        return withContext(Dispatchers.IO) {
            queueDao.getTracksOfQueue(queueId)?.tracks?.map { it.toTrackModel() } ?: emptyList()
        }
    }

    override fun getAllQueuesViaFlow(): Flow<List<QueueModel>> {
        return queueDao.getAllQueuesViaFlow().map { it ->
            it.map {
                it.toQueueModel(
                    count = queueDao.getQueueTracksCountWithLiveData(it.id),
                    imageId = queueDao.getQueueCoverImageIdWithLiveData(it.id)
                )
            }
        }
    }

    override suspend fun getAllQueuesViaLiveData(): LiveData<List<QueueModel>> {
        return queueDao.getAllQueuesViaFlow().map { it ->
            it.map {
                it.toQueueModel(
                    count = queueDao.getQueueTracksCountWithLiveData(it.id),
                    imageId = queueDao.getQueueCoverImageIdWithLiveData(it.id)
                )
            }
        }.asLiveData(Dispatchers.IO)
    }

    override suspend fun getAllQueues(): List<QueueModel> {
        val queues = withContext(Dispatchers.IO) {
            queueDao.getAllQueues()
        }
        return queues.map {
            it.toQueueModel(
                count = queueDao.getQueueTracksCountWithLiveData(it.id),
                imageId = queueDao.getQueueCoverImageIdWithLiveData(it.id)
            )
        }
    }

    override suspend fun getQueue(id: String): QueueModel {
        val queue = withContext(Dispatchers.IO) { queueDao.getQueue(id) }
        return queue.toQueueModel(
            count = queueDao.getQueueTracksCountWithLiveData(queue.id),
            imageId = queueDao.getQueueCoverImageIdWithLiveData(queue.id)
        )
    }
}