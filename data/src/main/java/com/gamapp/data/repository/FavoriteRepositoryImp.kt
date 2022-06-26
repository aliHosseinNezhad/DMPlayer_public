package com.gamapp.data.repository


import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.gamapp.data.dao.QueueDao
import com.gamapp.data.utils.DefaultQueueIds
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.repository.FavoriteRepository
import com.gamapp.domain.repository.QueueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoriteRepositoryImp @Inject constructor(
    private val queueRepository: QueueRepository,
    private val queueDao: QueueDao,
) : FavoriteRepository {
    val id = DefaultQueueIds.Favorite
    override suspend fun addToFavorite(trackModel: TrackModel) {
        queueRepository.addTrackToQueue(id, trackModel)
    }

    override suspend fun removeFromFavorite(fileId: Long) {
        queueRepository.removeTrackFromQueue(id, fileId)
    }

    override suspend fun removeFromFavorite(tracks: List<TrackModel>) {
        queueRepository.removeTracksFromQueue(tracks = tracks, queueId = id)
    }

    override suspend fun clear() {
        queueRepository.clearTracksFromQueue(id)
    }

    override fun getAllFavorites(): Flow<List<TrackModel>> {
        return queueRepository.getAllMusicByQueueIdViaFlow(id)
    }

    override fun isFavorite(fileId: Long): LiveData<Boolean> {
        return queueDao.isFavorite(fileId).map { it >= 1 }.asLiveData(Dispatchers.IO)
    }
}