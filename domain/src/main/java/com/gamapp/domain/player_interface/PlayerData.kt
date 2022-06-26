package com.gamapp.domain.player_interface

import com.gamapp.domain.models.PlayList
import com.gamapp.domain.models.TrackModel
import com.google.android.exoplayer2.Player
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

infix fun <T> MutableStateFlow<T>.emit(input: T) {
    tryEmit(input)
}

suspend infix fun <T> MutableStateFlow<T>.emitSuspended(input: T) {
    emit(input)
}

sealed class RepeatMode(val value: Int) {
    object OFF : RepeatMode(Player.REPEAT_MODE_OFF)
    object ONE : RepeatMode(Player.REPEAT_MODE_ONE)
    object ALL : RepeatMode(Player.REPEAT_MODE_ALL)
}


fun @Player.RepeatMode Int.toRepeatMode(): RepeatMode {
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
    val shuffle:StateFlow<Boolean>
    val repeatMode: StateFlow<RepeatMode>
    val currentTrack: StateFlow<TrackModel?>
    val playList: StateFlow<PlayList>
    fun addListener(listener: Player.Listener)
}