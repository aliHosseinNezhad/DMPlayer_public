package com.gamapp.domain.player_interface

import android.support.v4.media.session.PlaybackStateCompat
import com.gamapp.domain.models.BaseTrackModel
import com.gamapp.domain.models.DescriptionModel
import com.gamapp.domain.models.PlayList
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

infix fun <T> MutableStateFlow<T>.tryEmit(input: T) {
    tryEmit(input)
}

suspend infix fun <T> MutableStateFlow<T>.emit(input: T) {
    emit(input)
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


interface PlayerData {
    val isPlaying: StateFlow<Boolean>
    val duration: StateFlow<Long>
    val currentPosition: StateFlow<Long>
    val progress: Flow<Float>
    val shuffle: StateFlow<Boolean>
    val repeatMode: StateFlow<RepeatMode>
    val currentTrack: StateFlow<TrackModel?>
    val playList: StateFlow<List<TrackModel>>
}