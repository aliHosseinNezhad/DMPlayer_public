package com.gamapp.dmplayer.framework.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.TrackModel
import com.gamapp.domain.player_interface.PlayerEvents
import com.gamapp.domain.player_interface.RepeatMode
import com.gamapp.domain.player_interface.tryEmit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class PlayerDataImpl : PlayerEvents {
    override val isPlaying: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val duration: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val currentPosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    override val progress: Flow<Float> = MutableStateFlow(0f)
    override val shuffle: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val repeatMode: MutableStateFlow<RepeatMode> = MutableStateFlow(RepeatMode.OFF)
    override val currentTrack: MutableStateFlow<TrackModel?> = MutableStateFlow(null)
    override val playList: MutableStateFlow<List<TrackModel>> = MutableStateFlow(emptyList())

    private val callback = object : MediaControllerCompat.Callback() {
        val data = this@PlayerDataImpl
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
            if (state != null) {
                currentPosition tryEmit state.position
                val playbackState = state.state
                isPlaying tryEmit (playbackState == PlaybackStateCompat.STATE_PLAYING)
            }
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
            if (metadata != null) {
                val durationKey = MediaMetadataCompat.METADATA_KEY_DURATION
                val duration = metadata.getLong(durationKey)
                data.duration tryEmit duration
                val mediaIdKey = MediaMetadataCompat.METADATA_KEY_MEDIA_ID
                val item = try {
                    BaseTrackModel(
                        id = metadata.getString(mediaIdKey).toLong(),
                        title = metadata.description.title.toString(),
                        subtitle = metadata.description.subtitle.toString()
                    )
                } catch (e: Exception) {
                    null
                }
                TODO()
//                data.currentTrack tryEmit item
            }
        }

        override fun onQueueChanged(queue: MutableList<MediaSessionCompat.QueueItem>?) {
            super.onQueueChanged(queue)
            val medias = queue?.map {
                BaseTrackModel(
                    id= it.queueId,
                    title = it.description.title.toString(),
                    subtitle = it.description.subtitle.toString()
                )
            } ?: emptyList()
            TODO()
//            currentPlayList tryEmit medias
        }
    }


    fun register(controller: MediaControllerCompat) {
        controller.registerCallback(callback)
    }

    fun unregister(controller: MediaControllerCompat) {
        controller.unregisterCallback(callback)
    }
}