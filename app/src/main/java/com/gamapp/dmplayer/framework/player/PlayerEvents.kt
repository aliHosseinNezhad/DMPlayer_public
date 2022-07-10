package com.gamapp.dmplayer.framework.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.gamapp.domain.models.ArtistModel.Companion.empty
import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.*
import com.gamapp.domain.usecase.data.tracks.GetTracksByIdUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import java.util.concurrent.Executors


class PlayerEventImpl(
    private val getTracksByIdUseCase: GetTracksByIdUseCase,
    private val _controller: () -> MediaControllerCompat?
) : PlayerEvents {
    override val playbackState: MutableStateFlow<PlaybackState> =
        MutableStateFlow(PlaybackState.None)
    private val controller get() = _controller()
    override val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val duration: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val currentPosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val progress: Flow<Float> =
        currentPosition.combine(duration, transform = { position, duration ->
            (position / duration.toFloat()).coerceIn(0f, 1f)
        })
    override val shuffle: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val repeatMode: MutableStateFlow<RepeatMode> = MutableStateFlow(RepeatMode.OFF)
    override val currentTrack: MutableStateFlow<BaseTrack?> = MutableStateFlow(null)
    override val playList: MutableStateFlow<List<TrackModel>> = MutableStateFlow(emptyList())

    var scope: CoroutineScope? = null
        set(value) {
            value?.launch {
                setPosition()
            }
            field = value
        }

    private suspend fun setPosition() {
        val dispatcher = Executors.newSingleThreadScheduledExecutor().asCoroutineDispatcher()
        withContext(dispatcher) {
            isPlaying.collectLatest {
                while (it) {
                    delay(200)
                    val position = controller?.playbackState?.position ?: 0L
                    currentPosition.emit(position)
                }
            }
        }
    }

    private val callback = object : MediaControllerCompat.Callback() {
        val data = this@PlayerEventImpl
        var isPlaying = false
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            val playbackState = PlaybackState.create(state?.state)
            data.playbackState tryEmit playbackState

            val notPlaying = (playbackState is PlaybackState.None ||
                    playbackState is PlaybackState.Error ||
                    playbackState is PlaybackState.Paused ||
                    playbackState is PlaybackState.Stopped ||
                    playbackState is PlaybackState.NotInitialized)
            if (notPlaying) {
                this.isPlaying = false
            }
            val isPlaying = playbackState is PlaybackState.Playing
            if (isPlaying) {
                this.isPlaying = true
            }
            data.isPlaying tryEmit this.isPlaying
            currentPosition tryEmit (state?.position ?: 0L)
        }

        override fun onRepeatModeChanged(repeatMode: Int) {
            super.onRepeatModeChanged(repeatMode)
            data.repeatMode.tryEmit(RepeatMode.toRepeatMode(repeatMode))
        }

        override fun onShuffleModeChanged(shuffleMode: Int) {
            super.onShuffleModeChanged(shuffleMode)
            data.shuffle.tryEmit(shuffleMode != PlaybackStateCompat.SHUFFLE_MODE_NONE)
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
            metadata ?: return
            val duration = metadata.getLong(MediaMetadataCompat.METADATA_KEY_DURATION) ?: 0L
            data.duration tryEmit duration
            val id = try {
                metadata.description?.mediaId?.toLong()
            } catch (e: NumberFormatException) {
                return
            }
            scope?.launch {
                id ?: let {
                    data.currentTrack tryEmit null
                    return@launch
                }
                val track = getTracksByIdUseCase(id)
                data.currentTrack tryEmit track
            }
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            val tracks = queue?.mapNotNull {
                it.description.toTrackModel()
            } ?: emptyList()
            playList tryEmit tracks
        }
    }


    fun register(controller: MediaControllerCompat) {
        controller.registerCallback(callback)
    }

    fun unregister(controller: MediaControllerCompat) {
        controller.unregisterCallback(callback)
    }
}