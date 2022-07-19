package com.gamapp.dmplayer.framework.data_source

import android.content.Context
import com.gamapp.data.dao.MusicDao
import com.gamapp.data.data_source.media_store.MediaStoreFetchDataSource
import com.gamapp.data.mapper.toTrackEntity
import com.gamapp.dmplayer.framework.utils.binarySearch
import com.gamapp.dmplayer.framework.utils.mediastore_utils.getTracks
import com.gamapp.domain.mediaStore.MediaStoreChangeListener
import com.gamapp.domain.mediaStore.MediaStoreChangeNotifier
import com.gamapp.domain.models.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

const val Insert = "insert"
const val Remove = "remove"
const val Update = "update"
const val Current = "current"
const val New = "new"


sealed class LiveTracks {
    class Updated(val tracks: List<TrackModel>) : LiveTracks() {
        val albums: List<AlbumModel> by lazy {
            tracks.groupBy { it.albumId }.map { AlbumModel(it.key, it.value) }.sortedBy { it.id }
        }
        val artist: List<ArtistModel> by lazy {
            tracks.groupBy { it.artistId }.map { ArtistModel(it.key, it.value) }.sortedBy { it.id }
        }
    }

    object Expired : LiveTracks()
}

suspend inline fun MutableStateFlow<LiveTracks>.receive(
    context: Context,
    crossinline send: suspend (LiveTracks.Updated) -> Unit
) {
    collectLatest { liveTracks ->
        if (liveTracks is LiveTracks.Updated) {
            send(liveTracks)
        } else {
            val tracks = context.getTracks()
            emit(LiveTracks.Updated(tracks))
        }
    }
}

suspend inline fun MutableStateFlow<LiveTracks>.receiveTracks(
    context: Context,
    crossinline send: suspend (List<TrackModel>) -> Unit
) {
    receive(context) {
        send(it.tracks)
    }
}


@Singleton
class MediaStoreFetchDataSourceImpl @Inject constructor(
    @ApplicationContext context: Context,
    mediaStoreChangeNotifier: MediaStoreChangeNotifier,
) : AbstractMediaStoreFetchDataSource(context = context) {

    override val liveTracks = MutableStateFlow<LiveTracks>(LiveTracks.Expired)

    init {
        mediaStoreChangeNotifier.register(object : MediaStoreChangeListener {
            override fun onMediaStoreChanged() {
                liveTracks.tryEmit(LiveTracks.Expired)
            }
        })
    }

    private suspend fun updatePlayer(tracks: List<TrackModel>) {
//        TODO()
//        val musics = playerData.playList.value.tracks.map { it.copy() }.toMutableList()
//        val input = tracks.map { it to New }
//        val previous = musics.map { it to Current }
//        val sum = input + previous
//        val result = sum.groupBy {
//            it.first.id
//        }.mapNotNull {
//            if (it.value.size == 2) {
//                it.value.firstOrNull { it.second == New }!!.first to Update
//            } else {
//                val item = it.value.first()
//                if (item.second == Current) item.first to Remove
//                else
//                    null
//            }
//        }
//        val toDelete =
//            result.mapNotNull { if (it.second == Remove) it.first else null }.map { it.id }
//        val currentTrack = playerData.currentTrack.value
//        if (result.isEmpty())
//            playerConnector.setPlayList(emptyList())
//        else {
//            playerConnector.removePlayListItems(toDelete)
//        }
    }
}


abstract class AbstractMediaStoreFetchDataSource constructor(protected val context: Context) :
    MediaStoreFetchDataSource {
    protected abstract val liveTracks: MutableStateFlow<LiveTracks>

    override fun getAllTracks(): Flow<List<TrackModel>> {
        return channelFlow {
            liveTracks.receiveTracks(context = context) {
                send(it)
            }
        }
    }

    override fun getAllAlbums(): Flow<List<AlbumModel>> {
        return channelFlow {
            liveTracks.receive(context) {
                send(it.albums)
            }
        }
    }

    override fun getAllArtists(): Flow<List<ArtistModel>> {
        return channelFlow {
            liveTracks.receive(context) {
                send(it.artist)
            }
        }
    }

    override fun getAlbumById(albumId: Long): Flow<AlbumModel> {
        return getAllAlbums().map {
            val index = it.binarySearch(target = albumId, getKey = {
                it.id
            }, comparison = { value1, value2 ->
                value1.compareTo(value2)
            })
            if (index != null) it[index] else AlbumModel.empty
        }
    }

    override fun getArtistById(artistId: Long): Flow<ArtistModel> {
        return getAllArtists().map {
            val index = it.binarySearch(target = artistId, getKey = {
                it.id
            }, comparison = { value1, value2 ->
                value1.compareTo(value2)
            })
            if (index != null) it[index] else ArtistModel.empty
        }
    }

    override fun getTracksByIds(ids: List<Long>): Flow<List<TrackModel>> {
        return channelFlow {
            liveTracks.receiveTracks(context) { allTracks ->
                val tracks = allTracks.binarySearch(targets = ids.sortedBy { it },
                    getKey = { it.id },
                    comparison = { v1, v2 -> v1.compareTo(v2) }).mapNotNull {
                    it?.let {
                        allTracks[it]
                    }
                }
                send(tracks)
            }
        }
    }

    override fun getTrackById(id: Long): Flow<TrackModel?> {
        return channelFlow {
            liveTracks.receiveTracks(context) { allTracks ->
                val index = allTracks.binarySearch {
                    it.id.compareTo(id)
                }
                send(allTracks.getOrNull(index))
            }
        }
    }
}


/**
 * For Test Purpose
 * */
class FakeMediaStoreFetchDataSource constructor(context: Context) :
    AbstractMediaStoreFetchDataSource(context = context) {
    public override val liveTracks: MutableStateFlow<LiveTracks> =
        MutableStateFlow(LiveTracks.Expired)
}