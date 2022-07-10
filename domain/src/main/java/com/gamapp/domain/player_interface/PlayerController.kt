package com.gamapp.domain.player_interface

import com.gamapp.domain.models.BaseTrack
import com.gamapp.domain.models.TrackModel

interface PlayerController {
    fun play()
    fun pause()
    suspend fun forward(ml:Long = 100)
    suspend fun rewind(ml:Long = 100)
    fun skipToNext()
    fun skipToPrevious()
    suspend fun seekTo(ml: Long)
    fun setRepeatMode(mode: RepeatMode)
    fun shuffle(enable: Boolean)

    fun setCurrentTrack(track:BaseTrack)

    fun setPlayList(current: TrackModel, playList: List<TrackModel>) =
        setPlayList(current, playList, false)

    fun setPlayListAndPlay(current: TrackModel, playList: List<TrackModel>) =
        setPlayList(current, playList, true)

    fun setPlayList(current: TrackModel, playList: List<TrackModel>, playWhenReady: Boolean)
}