package com.gamapp.domain.player_interface

import android.support.v4.media.session.PlaybackStateCompat
import com.gamapp.domain.models.*
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


sealed class PlaybackState(val value: Int, val name: String) {
    object NotInitialized : PlaybackState(-1 , "NotInitialized")
    object None : PlaybackState(android.media.session.PlaybackState.STATE_NONE,"STATE_NONE")
    object Stopped : PlaybackState(android.media.session.PlaybackState.STATE_STOPPED,"STATE_STOPPED")
    object Playing : PlaybackState(android.media.session.PlaybackState.STATE_PLAYING,"STATE_PLAYING")
    object Paused : PlaybackState(android.media.session.PlaybackState.STATE_PAUSED,"STATE_PAUSED")
    object FastForwarding : PlaybackState(android.media.session.PlaybackState.STATE_FAST_FORWARDING,"STATE_FAST_FORWARDING")
    object Rewinding : PlaybackState(android.media.session.PlaybackState.STATE_REWINDING,"STATE_REWINDING")
    object Buffering : PlaybackState(android.media.session.PlaybackState.STATE_BUFFERING,"STATE_BUFFERING")
    object Error : PlaybackState(android.media.session.PlaybackState.STATE_ERROR,"STATE_ERROR")
    object Connecting : PlaybackState(android.media.session.PlaybackState.STATE_CONNECTING,"STATE_CONNECTING")
    object SkippingToPrevious :
        PlaybackState(android.media.session.PlaybackState.STATE_SKIPPING_TO_PREVIOUS,"STATE_SKIPPING_TO_PREVIOUS")

    object SkippingToNext :
        PlaybackState(android.media.session.PlaybackState.STATE_SKIPPING_TO_NEXT,"STATE_SKIPPING_TO_NEXT")

    object SkippingToQueueItem :
        PlaybackState(android.media.session.PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM,"STATE_SKIPPING_TO_QUEUE_ITEM")

    companion object {
        fun create(value: Int?): PlaybackState {
            return when (value) {
                null -> { NotInitialized }
                android.media.session.PlaybackState.STATE_NONE -> {
                    None
                }
                android.media.session.PlaybackState.STATE_STOPPED -> {
                    Stopped
                }
                android.media.session.PlaybackState.STATE_PLAYING -> {
                    Playing
                }
                android.media.session.PlaybackState.STATE_PAUSED -> {
                    Paused
                }
                android.media.session.PlaybackState.STATE_FAST_FORWARDING -> {
                    FastForwarding
                }
                android.media.session.PlaybackState.STATE_REWINDING -> {
                    Rewinding
                }
                android.media.session.PlaybackState.STATE_BUFFERING -> {
                    Buffering
                }
                android.media.session.PlaybackState.STATE_ERROR -> {
                    Error
                }
                android.media.session.PlaybackState.STATE_CONNECTING -> {
                    Connecting
                }
                android.media.session.PlaybackState.STATE_SKIPPING_TO_PREVIOUS -> {
                    SkippingToPrevious
                }
                android.media.session.PlaybackState.STATE_SKIPPING_TO_NEXT -> {
                    SkippingToNext
                }
                android.media.session.PlaybackState.STATE_SKIPPING_TO_QUEUE_ITEM -> {
                    SkippingToQueueItem
                }
                else -> { None }
            }
        }
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        return (other as? PlaybackState)?.value == this.value
    }

}

sealed class RepeatMode(val value: Int) {
    object OFF : RepeatMode(PlaybackStateCompat.REPEAT_MODE_NONE)
    object ONE : RepeatMode(PlaybackStateCompat.REPEAT_MODE_ONE)
    object ALL : RepeatMode(PlaybackStateCompat.REPEAT_MODE_ALL)
    object Group : RepeatMode(PlaybackStateCompat.REPEAT_MODE_GROUP)
    companion object {
        fun toRepeatMode(value:Int): RepeatMode {
            return when (value) {
                Player.REPEAT_MODE_OFF -> OFF
                Player.REPEAT_MODE_ONE -> ONE
                Player.REPEAT_MODE_ALL -> ALL
                else -> OFF
            }
        }
    }
}


fun Int.toRepeatMode(): RepeatMode {
    return when (this) {
        Player.REPEAT_MODE_OFF -> RepeatMode.OFF
        Player.REPEAT_MODE_ONE -> RepeatMode.ONE
        Player.REPEAT_MODE_ALL -> RepeatMode.ALL
        else -> RepeatMode.OFF
    }
}


interface PlayerEvents {
    val playbackState:StateFlow<PlaybackState>
    val isPlaying: StateFlow<Boolean>
    val duration: StateFlow<Long>
    val currentPosition: StateFlow<Long>
    val progress: Flow<Float>
    val shuffle: StateFlow<Boolean>
    val repeatMode: StateFlow<RepeatMode>
    val currentTrack: StateFlow<BaseTrack?>
    val playList: StateFlow<List<TrackModel>>
}