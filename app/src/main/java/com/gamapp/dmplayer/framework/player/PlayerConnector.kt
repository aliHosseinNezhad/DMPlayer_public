package com.gamapp.dmplayer.framework.player

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat
import com.gamapp.data.mapper.toList
import com.gamapp.dmplayer.framework.service.MusicService
import com.gamapp.domain.player_interface.PlayerConnector
import com.gamapp.domain.player_interface.RepeatMode
import com.gamapp.domain.player_interface.emit
import com.gamapp.domain.models.PlayList
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.models.toShuffleOrder
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.upstream.DefaultDataSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "PlayerConnectorImplTAG"

@Singleton
class PlayerConnectorImpl @Inject constructor(
    private val factory: DefaultDataSource.Factory,
    private val data: PlayerDataImpl,
    @ApplicationContext private val context: Context
) : PlayerConnector {
    var player: ExoPlayer? = null
    var scope: CoroutineScope? = null
    lateinit var mediaBrowser: MediaBrowserCompat


    fun onCreate(player: ExoPlayer, serviceScope: CoroutineScope) {
        this.player = player
        this.scope = serviceScope
    }

    fun onDestroy() {
        this.player = null
        this.scope = null
    }


    override suspend fun play() {
        withContext(Dispatchers.Main) {
            player?.playWhenReady = true
        }
    }


    override suspend fun pause() {
        withContext(Dispatchers.Main) {
            player?.playWhenReady = false
        }
    }

    override suspend fun setRepeatMode(mode: RepeatMode) {
        withContext(Dispatchers.Main) {
            player?.repeatMode = mode.value
        }
    }

    override suspend fun shuffleMode(enable: Boolean) {
        withContext(Dispatchers.Main) {
            player?.shuffleModeEnabled = enable
        }
    }

    override suspend fun seekTo(seek: Long) {
        withContext(Dispatchers.Main) {
            player?.let { player ->
                if (player.currentMediaItem != null) {
                    player.seekTo(seek)
                }
            }
        }
    }

    override suspend fun seekTo(index: Int, seek: Long) {
        withContext(Dispatchers.Main) {
            player?.seekTo(index, seek)
        }
    }

    override suspend fun stop() {
        withContext(Dispatchers.Main) {
            player?.stop()
        }
    }

    override suspend fun nextMusic() {
        withContext(Dispatchers.Main) {
            player?.seekToNext()
        }

    }

    override suspend fun previousMusic() {
        withContext(Dispatchers.Main) {
            player?.seekToPrevious()
        }
    }

    override suspend fun setPlayList(
        playList: List<TrackModel>,
        onSetCurrentMusic: suspend (ExoPlayer) -> Unit
    ) {
        withContext(Dispatchers.Main) {
            if (playList != data.playList.value.tracks) {
                data.concatenatingMediaSource.clear()
                val songs = playList.mapNotNull { it.toMediaMetaData() }
                    .asMediaSource(factory, data.concatenatingMediaSource)
                data.playList emit PlayList(tracks = playList, shuffle = data.shuffle.value).also {
                    data.concatenatingMediaSource.setShuffleOrder(
                        it.toShuffleOrder()
                    )
                }
                player?.setMediaSource(songs)
                player?.let {
                    onSetCurrentMusic(it)
                }
                player?.prepare()
            } else {
                player?.let {
                    onSetCurrentMusic(it)
                }
            }
        }
    }

    override suspend fun setPlayerListAndCurrent(track: TrackModel, playList: List<TrackModel>) {
        val isPlaying = player?.isPlaying == true
        setPlayList(playList) {
            setCurrentMusic(track)
        }
        withContext(Dispatchers.Main) {
            player?.playWhenReady = isPlaying
        }
    }


    private suspend fun setCurrentMusic(track: TrackModel) {
        withContext(Dispatchers.Main) {
            val index = data.playList.value.tracks.indexOf(track)
            if (index == -1) return@withContext
            player?.seekTo(index, 0L)
        }
    }

    override suspend fun setPlayListAndPlay(track: TrackModel, playList: List<TrackModel>) {
        setPlayList(playList) {
            if (playList.isNotEmpty())
                setCurrentMusic(track)
        }
        withContext(Dispatchers.Main) {
            player?.playWhenReady = true
        }
    }

    override suspend fun removePlayListItems(items: List<Long>) {
        val sources = data.concatenatingMediaSource.toList()
        val toDelete = sources.filter {
            it.mediaItem.mediaId.toLong() in items
        }.mapNotNull { it ->
            sources.indexOf(it).let {
                if (it == -1) null else it
            }
        }
        val pl = data.playList.value.tracks
        data.playList emit data.playList.value.copy(pl.filter {
            it.id !in items
        })
        toDelete.forEach { it ->
            data.concatenatingMediaSource.removeMediaSource(it)
            data.playList.value.toShuffleOrder().let {
                data.concatenatingMediaSource.setShuffleOrder(it)
            }
        }
    }
}
